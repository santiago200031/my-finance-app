package com.mobilecomputing.myfinance.data

import android.content.Context
import com.mobilecomputing.myfinance.data.models.User
import com.mobilecomputing.myfinance.data.repository.CategoryRepository
import com.mobilecomputing.myfinance.data.repository.ContractRepository
import com.mobilecomputing.myfinance.data.repository.EntryRepository
import com.mobilecomputing.myfinance.data.repository.ReminderRepository
import com.mobilecomputing.myfinance.data.repository.UserPreferencesRepository
import com.mobilecomputing.myfinance.data.repository.UserRepository
import com.mobilecomputing.myfinance.data.repository.impl.FakeCategoryRepository
import com.mobilecomputing.myfinance.data.repository.impl.FirestoreContractRepository
import com.mobilecomputing.myfinance.data.repository.impl.FirestoreEntryRepository
import com.mobilecomputing.myfinance.data.repository.impl.FirestoreReminderRepository
import com.mobilecomputing.myfinance.data.repository.impl.FirestoreUserRepository
import com.mobilecomputing.myfinance.data.service.ContractService
import com.mobilecomputing.myfinance.data.service.EntryService

interface AppContainer {
        val entryRepository: EntryRepository
        val categoryRepository: CategoryRepository
        val contractRepository: ContractRepository
        val userRepository: UserRepository
        val contractService: ContractService
        val reminderRepository: ReminderRepository
        val entryService: EntryService
}

class DefaultAppContainer(private val context: Context) : AppContainer {
        private val userPreferencesRepository by lazy { UserPreferencesRepository(context) }

        override val entryRepository: EntryRepository by lazy {
                FirestoreEntryRepository(userPreferencesRepository)
        }
        override val entryService: EntryService by lazy { EntryService(entryRepository) }
        override val categoryRepository: CategoryRepository by lazy { FakeCategoryRepository() }
        override val contractRepository: ContractRepository by lazy {
                FirestoreContractRepository(userPreferencesRepository)
        }
        override val userRepository: UserRepository by lazy {
                FirestoreUserRepository(
                        initialUsers =
                                listOf(
                                        User(
                                                id = "s-svilla",
                                                email = "s-svilla@haw-landshut.de",
                                                firstName = "Santiago",
                                                lastName = "Villavicencio"
                                        ),
                                        User(
                                                id = "villavicencioandrs",
                                                email = "villavicencioandrs@gmail.com",
                                                firstName = "Andrés",
                                                lastName = "Villavicencio"
                                        )
                                ),
                        userPreferencesRepository = userPreferencesRepository
                )
        }
        override val contractService: ContractService by lazy {
                ContractService(contractRepository)
        }
        override val reminderRepository: ReminderRepository by lazy { 
                FirestoreReminderRepository(userPreferencesRepository = userPreferencesRepository) 
        }
}
