package com.example.contactapp.viewmodels.contactlist

import app.cash.turbine.test
import com.example.contactapp.domain.model.Contact
import com.example.contactapp.domain.usecase.DeleteAllContactsUseCase
import com.example.contactapp.domain.usecase.DeleteContactUseCase
import com.example.contactapp.domain.usecase.GetContactsUseCase
import com.example.contactapp.view.ui.screens.contactlist.ContactListUiState
import com.example.contactapp.viewmodel.ContactListEvent
import com.example.contactapp.viewmodel.ContactListViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContactListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getContactsUseCase: GetContactsUseCase
    private lateinit var deleteContactUseCase: DeleteContactUseCase
    private lateinit var deleteAllContactsUseCase: DeleteAllContactsUseCase
    private lateinit var viewModel: ContactListViewModel

    private val sampleContacts = listOf(
        Contact(id = 1, firstName = "Ana", lastName = "García", phone = "1234567", imageUrl = ""),
        Contact(id = 2, firstName = "Luis", lastName = "Pérez", phone = "7654321", imageUrl = ""),
        Contact(id = 3, firstName = "Carlos", lastName = "López", phone = "1112233", imageUrl = "")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getContactsUseCase = mockk()
        deleteContactUseCase = mockk()
        deleteAllContactsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when contacts exist uiState emits Success`() = runTest {
        every { getContactsUseCase() } returns flowOf(sampleContacts)
        viewModel = ContactListViewModel(getContactsUseCase, deleteContactUseCase, deleteAllContactsUseCase)

        viewModel.uiState.test {
            // Skip Loading
            skipItems(1)
            val state = awaitItem()
            assertTrue(state is ContactListUiState.Success)
            assertEquals(3, (state as ContactListUiState.Success).contacts.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when no contacts uiState emits Empty`() = runTest {
        every { getContactsUseCase() } returns flowOf(emptyList())
        viewModel = ContactListViewModel(getContactsUseCase, deleteContactUseCase, deleteAllContactsUseCase)

        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem()
            assertTrue(state is ContactListUiState.Empty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search filters contacts by firstName`() = runTest {
        every { getContactsUseCase() } returns flowOf(sampleContacts)
        viewModel = ContactListViewModel(getContactsUseCase, deleteContactUseCase, deleteAllContactsUseCase)

        viewModel.uiState.test {
            skipItems(1)
            skipItems(1)

            viewModel.onSearchQueryChanged("ana")
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is ContactListUiState.Success)
            val contacts = (state as ContactListUiState.Success).contacts
            assertEquals(1, contacts.size)
            assertEquals("Ana", contacts.first().firstName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search filters contacts by phone`() = runTest {
        every { getContactsUseCase() } returns flowOf(sampleContacts)
        viewModel = ContactListViewModel(getContactsUseCase, deleteContactUseCase, deleteAllContactsUseCase)

        viewModel.uiState.test {
            skipItems(1) // Loading
            skipItems(1) // Success(all 3 contacts)

            viewModel.onSearchQueryChanged("765")
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is ContactListUiState.Success)
            val contacts = (state as ContactListUiState.Success).contacts
            assertEquals(1, contacts.size)
            assertEquals("Luis", contacts.first().firstName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteAllContacts emits snackbar event on failure`() = runTest {
        every { getContactsUseCase() } returns flowOf(sampleContacts)
        coEvery { deleteAllContactsUseCase() } returns Result.failure(Exception("DB error"))
        viewModel = ContactListViewModel(getContactsUseCase, deleteContactUseCase, deleteAllContactsUseCase)

        viewModel.events.test {
            viewModel.deleteAllContacts()
            testDispatcher.scheduler.advanceUntilIdle()
            val event = awaitItem()
            assertTrue(event is ContactListEvent.ShowSnackbar)
            assertTrue((event as ContactListEvent.ShowSnackbar).message.contains("DB error"))
            cancelAndIgnoreRemainingEvents()
        }
    }
}

