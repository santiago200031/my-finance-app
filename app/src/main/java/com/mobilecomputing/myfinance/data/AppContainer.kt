package com.mobilecomputing.myfinance.data

import android.content.Context
import com.mobilecomputing.myfinance.data.repository.CategoryRepository
import com.mobilecomputing.myfinance.data.repository.ContractRepository
import com.mobilecomputing.myfinance.data.repository.EntryRepository
import com.mobilecomputing.myfinance.data.repository.ReminderRepository
import com.mobilecomputing.myfinance.data.repository.UserPreferencesRepository
import com.mobilecomputing.myfinance.data.repository.UserRepository
import com.mobilecomputing.myfinance.data.repository.impl.FakeCategoryRepository
import com.mobilecomputing.myfinance.data.repository.impl.FakeContractRepository
import com.mobilecomputing.myfinance.data.repository.impl.FakeEntryRepository
import com.mobilecomputing.myfinance.data.repository.impl.FakeReminderRepository
import com.mobilecomputing.myfinance.data.repository.impl.FakeUserRepository
import com.mobilecomputing.myfinance.domain.ContractService

interface AppContainer {
    val entryRepository: EntryRepository
    val categoryRepository: CategoryRepository
    val contractRepository: ContractRepository
    val userRepository: UserRepository
    val contractService: ContractService
    val reminderRepository: ReminderRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val entryRepository: EntryRepository by lazy { FakeEntryRepository() }
    override val categoryRepository: CategoryRepository by lazy { FakeCategoryRepository() }
    override val contractRepository: ContractRepository by lazy { FakeContractRepository() }
    override val userRepository: UserRepository by lazy {
        FakeUserRepository(UserPreferencesRepository(context))
    }
    override val contractService: ContractService by lazy { ContractService() }
    override val reminderRepository: ReminderRepository by lazy { FakeReminderRepository() }
}
