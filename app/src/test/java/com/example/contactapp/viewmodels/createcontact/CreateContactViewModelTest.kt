package com.example.contactapp.viewmodels.createcontact

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.contactapp.domain.model.Contact
import com.example.contactapp.domain.usecase.AddContactUseCase
import com.example.contactapp.domain.usecase.GetContactByIdUseCase
import com.example.contactapp.domain.usecase.GetRandomImageUrlUseCase
import com.example.contactapp.domain.usecase.UpdateContactUseCase
import com.example.contactapp.view.ui.screens.createcontact.CreateContactEvent
import com.example.contactapp.viewmodel.CreateContactViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateContactViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var addContactUseCase: AddContactUseCase
    private lateinit var updateContactUseCase: UpdateContactUseCase
    private lateinit var getContactByIdUseCase: GetContactByIdUseCase
    private lateinit var getRandomImageUrlUseCase: GetRandomImageUrlUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        addContactUseCase = mockk()
        updateContactUseCase = mockk()
        getContactByIdUseCase = mockk()
        getRandomImageUrlUseCase = mockk()
        coEvery { getRandomImageUrlUseCase() } returns
                Result.success("https://picsum.photos/id/1/400/400")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModelForCreate(): CreateContactViewModel =
        CreateContactViewModel(
            addContactUseCase,
            updateContactUseCase,
            getContactByIdUseCase,
            getRandomImageUrlUseCase,
            SavedStateHandle()   // no contactId → create mode
        )

    private fun createViewModelForEdit(contactId: Int): CreateContactViewModel =
        CreateContactViewModel(
            addContactUseCase,
            updateContactUseCase,
            getContactByIdUseCase,
            getRandomImageUrlUseCase,
            SavedStateHandle(mapOf("contactId" to contactId))
        )

    @Test
    fun `create mode — init loads random image and updates imageUrl`() = runTest {
        val vm = createViewModelForCreate()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("https://picsum.photos/id/1/400/400", vm.uiState.value.imageUrl)
        assertTrue(!vm.uiState.value.isLoadingImage)
        assertNull(vm.uiState.value.imageError)
        assertTrue(!vm.uiState.value.isEditMode)
    }

    @Test
    fun `create mode — network failure sets imageError`() = runTest {
        coEvery { getRandomImageUrlUseCase() } returns Result.failure(Exception("timeout"))
        val vm = createViewModelForCreate()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(vm.uiState.value.imageError)
    }

    @Test
    fun `create mode — saveContact with blank fields shows field errors`() = runTest {
        val vm = createViewModelForCreate()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.saveContact()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(vm.uiState.value.firstNameError)
        assertNotNull(vm.uiState.value.lastNameError)
        assertNotNull(vm.uiState.value.phoneError)
    }

    @Test
    fun `create mode — saveContact with invalid phone shows phone error`() = runTest {
        val vm = createViewModelForCreate()
        vm.onFirstNameChanged("Ana")
        vm.onLastNameChanged("García")
        vm.onPhoneChanged("abc")
        vm.saveContact()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(vm.uiState.value.phoneError)
    }

    @Test
    fun `create mode — saveContact success emits NavigateBack`() = runTest {
        coEvery { addContactUseCase(any<Contact>()) } returns Result.success(Unit)
        val vm = createViewModelForCreate()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.onFirstNameChanged("Ana")
        vm.onLastNameChanged("García")
        vm.onPhoneChanged("1234567890")

        vm.events.test {
            vm.saveContact()
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is CreateContactEvent.ShowSnackbar)
            assertTrue(awaitItem() is CreateContactEvent.NavigateBack)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `edit mode — isEditMode is true and form is prefilled`() = runTest {
        val contact = Contact(id = 42, firstName = "Luis", lastName = "Pérez",
            phone = "5551234567", imageUrl = "https://picsum.photos/id/5/400/400")
        coEvery { getContactByIdUseCase(42) } returns Result.success(contact)

        val vm = createViewModelForEdit(contactId = 42)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state.isEditMode)
        assertEquals("Luis", state.firstName)
        assertEquals("Pérez", state.lastName)
        assertEquals("5551234567", state.phone)
    }

    @Test
    fun `edit mode — saveContact calls updateContactUseCase`() = runTest {
        val contact = Contact(id = 42, firstName = "Luis", lastName = "Pérez",
            phone = "5551234567", imageUrl = "")
        coEvery { getContactByIdUseCase(42) } returns Result.success(contact)
        coEvery { updateContactUseCase(any<Contact>()) } returns Result.success(Unit)

        val vm = createViewModelForEdit(contactId = 42)
        testDispatcher.scheduler.advanceUntilIdle()

        vm.events.test {
            vm.saveContact()
            testDispatcher.scheduler.advanceUntilIdle()
            val snackbar = awaitItem() as CreateContactEvent.ShowSnackbar
            assertTrue(snackbar.message.contains("actualizado"))
            assertTrue(awaitItem() is CreateContactEvent.NavigateBack)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `create mode — saveContact failure emits ShowSnackbar`() = runTest {
        coEvery { addContactUseCase(any<Contact>()) } returns
                Result.failure(Exception("DB error"))
        val vm = createViewModelForCreate()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.onFirstNameChanged("Ana")
        vm.onLastNameChanged("García")
        vm.onPhoneChanged("1234567890")

        vm.events.test {
            vm.saveContact()
            testDispatcher.scheduler.advanceUntilIdle()
            val event = awaitItem() as CreateContactEvent.ShowSnackbar
            assertTrue(event.message.contains("DB error"))
            cancelAndIgnoreRemainingEvents()
        }
    }
}
