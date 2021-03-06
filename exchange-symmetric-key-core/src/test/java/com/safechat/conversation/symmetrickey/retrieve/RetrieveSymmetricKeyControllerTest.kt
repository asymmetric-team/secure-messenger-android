package com.safechat.conversation.symmetrickey.retrieve

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.safechat.conversation.symmetrickey.retrieve.RetrieveSymmetricKeyController.RetrieveResult
import com.safechat.conversation.symmetrickey.retrieve.RetrieveSymmetricKeyController.RetrieveResult.KEY_NOT_PRESENT
import com.safechat.conversation.symmetrickey.retrieve.RetrieveSymmetricKeyController.RetrieveResult.KEY_RETRIEVED
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.Observable.error
import rx.Observable.just
import rx.observers.TestSubscriber

class RetrieveSymmetricKeyControllerTest {

    val decryptor = mock<RetrieveSymmetricKeyDecryptor>()
    val repository = mock<RetrieveSymmetricKeyRepository>()
    val service = mock<RetrieveSymmetricKeyService>()
    val controller = RetrieveSymmetricKeyControllerImpl(service, repository, decryptor)
    val subscriber = TestSubscriber<RetrieveResult>()

    @Before
    fun setUp() {
        stubRepository()
        stubService(just(null))
        stubDecryptor(just("decryptedSymmetricKey"))
    }

    @Test
    fun shouldReturnKeyNotPresent() {
        startController()
        subscriber.assertValue(KEY_NOT_PRESENT)
    }

    @Test
    fun shouldReturnKeyRetrieved() {
        stubService(just("encryptedSymmetricKey"))
        startController()
        subscriber.assertValue(KEY_RETRIEVED)
    }

    @Test
    fun shouldDecryptSymmetricKey() {
        stubService(just("encryptedSymmetricKey"))
        startController()
        verify(decryptor).decryptSymmetricKey(any(), any(), any())
    }

    @Test
    fun shouldSaveDecryptedSymmetricKey() {
        stubService(just("encryptedSymmetricKey"))
        startController()
        verify(repository).saveDecryptedSymmetricKey("otherPublicKey", "decryptedSymmetricKey")
    }

    @Test
    fun shouldReturnErrorWhenApiFails() {
        val exception = RuntimeException()
        stubService(error(exception))
        startController()
        subscriber.assertError(exception)
    }

    @Test
    fun shouldReturnErrorWhenDecryptionFails() {
        val exception = RuntimeException()
        stubDecryptor(error(exception))
        stubService(just("encryptedSymmetricKey"))
        startController()
        subscriber.assertError(exception)
    }

    private fun startController() {
        controller.retrieveKey("otherPublicKey").subscribe(subscriber)
    }

    private fun stubRepository() {
        whenever(repository.getPublicKeyString()).thenReturn("myPublicKey")
        whenever(repository.getPrivateKeyString()).thenReturn("myPrivateKey")
    }

    private fun stubService(encryptedSymmetricKey: Observable<String?>) {
        whenever(service.retrieveSymmetricKey("myPublicKey", "otherPublicKey")).thenReturn(encryptedSymmetricKey)
    }

    private fun stubDecryptor(decryptionResult: Observable<String>?) {
        whenever(decryptor.decryptSymmetricKey("otherPublicKey", "myPrivateKey", "encryptedSymmetricKey")).thenReturn(decryptionResult)
    }
}
