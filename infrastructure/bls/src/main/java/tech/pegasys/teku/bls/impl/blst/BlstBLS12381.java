/*
 * Copyright Consensys Software Inc., 2025
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package tech.pegasys.teku.bls.impl.blst;

import static tech.pegasys.teku.bls.impl.blst.HashToCurve.ETH2_DST;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import org.apache.tuweni.bytes.Bytes48;
import supranational.blst.BLST_ERROR;
import supranational.blst.P2;
import supranational.blst.P2_Affine;
import supranational.blst.Pairing;
import tech.pegasys.teku.bls.BatchSemiAggregate;
import tech.pegasys.teku.bls.impl.BLS12381;
import tech.pegasys.teku.bls.impl.BlsException;
import tech.pegasys.teku.bls.impl.KeyPair;
import tech.pegasys.teku.bls.impl.PublicKey;
import tech.pegasys.teku.bls.impl.Signature;
import tech.pegasys.teku.infrastructure.crypto.SecureRandomProvider;

public class BlstBLS12381 implements BLS12381 {

  private static final int BATCH_RANDOM_BYTES = 8;
  // Note: SecureRandom is thread-safe.
  // We avoid creating new instances as that reads from /dev/random which may block on entropy
  private static final SecureRandom RANDOM = SecureRandomProvider.createSecureRandom();

  private static Random getRND() {
    return RANDOM;
  }

  public static BlstSignature sign(final BlstSecretKey secretKey, final Bytes message) {
    return sign(secretKey, message, HashToCurve.ETH2_DST);
  }

  public static BlstSignature sign(
      final BlstSecretKey secretKey, final Bytes message, final String dst) {
    if (secretKey.isZero()) {
      throw new IllegalArgumentException("Signing with zero private key is prohibited");
    }

    P2 p2 = new P2();
    p2.hash_to(message.toArrayUnsafe(), dst, new byte[0]).sign_with(secretKey.getKey());

    P2_Affine p2Affine = p2.to_affine();
    return new BlstSignature(p2Affine);
  }

  public static boolean verify(
      final BlstPublicKey publicKey, final Bytes message, final BlstSignature signature) {
    return verify(publicKey, message, signature, HashToCurve.ETH2_DST);
  }

  public static boolean verify(
      final BlstPublicKey publicKey,
      final Bytes message,
      final BlstSignature signature,
      final String dst) {
    BLST_ERROR res =
        signature.ec2Point.core_verify(publicKey.ecPoint, true, message.toArrayUnsafe(), dst);
    return res == BLST_ERROR.BLST_SUCCESS;
  }

  @Override
  public KeyPair generateKeyPair(final Random random) {
    BlstSecretKey secretKey = BlstSecretKey.generateNew(random);
    return new KeyPair(secretKey);
  }

  @Override
  public BlstPublicKey publicKeyFromCompressed(final Bytes48 compressedPublicKeyBytes) {
    return BlstPublicKey.fromBytes(compressedPublicKeyBytes);
  }

  @Override
  public BlstSignature signatureFromCompressed(final Bytes compressedSignatureBytes) {
    return BlstSignature.fromBytes(compressedSignatureBytes);
  }

  @Override
  public BlstSecretKey secretKeyFromBytes(final Bytes32 secretKeyBytes) {
    return BlstSecretKey.fromBytes(secretKeyBytes);
  }

  @Override
  public BlstPublicKey aggregatePublicKeys(final List<? extends PublicKey> publicKeys) {
    return BlstPublicKey.aggregate(publicKeys.stream().map(BlstPublicKey::fromPublicKey).toList());
  }

  @Override
  public BlstSignature aggregateSignatures(final List<? extends Signature> signatures) {
    return BlstSignature.aggregate(signatures.stream().map(BlstSignature::fromSignature).toList());
  }

  @Override
  public BlstSemiAggregate prepareBatchVerify(
      final int index,
      final List<? extends PublicKey> publicKeys,
      final Bytes message,
      final Signature signature) {

    BlstPublicKey aggrPubKey = aggregatePublicKeys(publicKeys);
    BlstSignature blstSignature = BlstSignature.fromSignature(signature);

    return blstPrepareBatchVerify(aggrPubKey, message, blstSignature);
  }

  BlstSemiAggregate blstPrepareBatchVerify(
      final BlstPublicKey pubKey, final Bytes message, final BlstSignature blstSignature) {

    Pairing ctx = new Pairing(true, ETH2_DST);
    BLST_ERROR ret =
        ctx.mul_n_aggregate(
            pubKey.ecPoint, blstSignature.ec2Point, nextBatchRandomMultiplier(), message.toArray());

    if (ret != BLST_ERROR.BLST_SUCCESS) {
      if (ret == BLST_ERROR.BLST_PK_IS_INFINITY) {
        return BlstSemiAggregate.createInvalid();
      } else {
        throw new BlsException("Error in Blst, error code: " + ret);
      }
    }

    ctx.commit();

    return new BlstSemiAggregate(ctx);
  }

  @Override
  public BatchSemiAggregate prepareBatchVerify2(
      final int index,
      final List<? extends PublicKey> publicKeys1,
      final Bytes message1,
      final Signature signature1,
      final List<? extends PublicKey> publicKeys2,
      final Bytes message2,
      final Signature signature2) {
    BlstSemiAggregate aggregate1 = prepareBatchVerify(index, publicKeys1, message1, signature1);
    BlstSemiAggregate aggregate2 = prepareBatchVerify(index + 1, publicKeys2, message2, signature2);

    aggregate1.mergeWith(aggregate2);
    return aggregate1;
  }

  @Override
  public boolean completeBatchVerify(final List<? extends BatchSemiAggregate> preparedList) {
    if (preparedList.isEmpty()) {
      return true;
    }

    try {
      final BlstSemiAggregate first = (BlstSemiAggregate) preparedList.get(0);
      if (!first.isValid()) {
        return false;
      }
      Pairing ctx0 = first.getCtx();
      for (int i = 1; i < preparedList.size(); i++) {
        final BlstSemiAggregate semiAggregate = (BlstSemiAggregate) preparedList.get(i);
        if (!semiAggregate.isValid()) {
          return false;
        }
        BLST_ERROR ret = ctx0.merge(semiAggregate.getCtx());
        if (ret != BLST_ERROR.BLST_SUCCESS) {
          return false;
        }
      }

      return ctx0.finalverify();
    } catch (final ClassCastException e) {
      // One of the semi aggregates was invalid and not a BlstSemiAggregate
      return false;
    }
  }

  static BigInteger nextBatchRandomMultiplier() {
    byte[] scalarBytes = new byte[BATCH_RANDOM_BYTES];
    getRND().nextBytes(scalarBytes);
    return new BigInteger(1, scalarBytes).add(BigInteger.ONE);
  }
}
