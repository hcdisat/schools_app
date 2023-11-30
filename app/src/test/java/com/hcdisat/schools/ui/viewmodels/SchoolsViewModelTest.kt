package com.hcdisat.schools.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.hcdisat.schools.dataaccess.network.ISchoolApiRepository
import com.hcdisat.schools.ui.schoollist.SchoolsViewModel
import com.hcdisat.schools.ui.state.RequestState
import com.hcdisat.schools.ui.state.RequestState.*
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SchoolsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var schoolsViewModel: SchoolsViewModel
    private val schoolRepo = mockk<ISchoolApiRepository>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `schoolData must be in a loading state`() = runTest {
        coEvery { schoolRepo.schoolRequestState } returns
            MutableStateFlow(INITIAL)
        schoolsViewModel = SchoolsViewModel(schoolRepo)

        schoolsViewModel.schoolData.observeForever {
            assertThat(it).isInstanceOf(INITIAL::class.java)
        }
    }

    @Test
    fun `schoolData must be in a SUCCESS state after init`() = runTest {
        coEvery { schoolRepo.schoolRequestState } returns
            MutableStateFlow(SUCCESS<RequestState>(mockk()))

        schoolsViewModel = SchoolsViewModel(schoolRepo)
        schoolsViewModel.schoolData.observeForever {
            assertThat(it).isInstanceOf(SUCCESS::class.java)
        }
    }

    @Test
    fun `schoolDetails must be in a loading state`() = runTest {
        coEvery { schoolRepo.schoolRequestState } returns
            MutableStateFlow(SUCCESS<RequestState>(mockk()))
        schoolsViewModel = SchoolsViewModel(schoolRepo)

        schoolsViewModel.schoolDetails.observeForever {
            assertThat(it).isInstanceOf(INITIAL::class.java)
        }
    }

    @Test
    fun `schoolDetails must be in a ERROR state after invoking getSchoolDetails`() = runTest {
        val dbn = "dbn"
        coEvery { schoolRepo.schoolRequestState } returns
            MutableStateFlow(SUCCESS<RequestState>(mockk()))
        coEvery { schoolRepo.getSchoolDetails(dbn) } returns mockk()
        coEvery { schoolRepo.detailsRequestState } returns MutableStateFlow(ERROR(mockk()))

        schoolsViewModel = SchoolsViewModel(schoolRepo)
        schoolsViewModel.getSchoolDetails(dbn)
        schoolsViewModel.schoolDetails.observeForever {
            assertThat(it).isInstanceOf(ERROR::class.java)
        }
    }

    @Test
    fun `schoolDetails must be in a SUCCESS state after invoking getSchoolDetails`() = runTest {
        val dbn = "dbn"
        coEvery { schoolRepo.schoolRequestState } returns
            MutableStateFlow(SUCCESS<RequestState>(mockk()))

        coEvery { schoolRepo.getSchoolDetails(dbn) } returns mockk()
        coEvery { schoolRepo.detailsRequestState } returns
            MutableStateFlow(SUCCESS<RequestState>(mockk()))

        schoolsViewModel = SchoolsViewModel(schoolRepo)
        schoolsViewModel.getSchoolDetails(dbn)
        schoolsViewModel.schoolDetails.observeForever {
            assertThat(it).isInstanceOf(SUCCESS::class.java)
        }
    }
}