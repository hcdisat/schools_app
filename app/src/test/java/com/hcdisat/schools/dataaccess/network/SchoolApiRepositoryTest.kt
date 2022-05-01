package com.hcdisat.schools.dataaccess.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.hcdisat.schools.dataaccess.database.IDbRepository
import com.hcdisat.schools.ui.state.RequestState.*
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SchoolApiRepositoryTest {

    @get:Rule
    val instantTaskExecution = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var schoolsRepo: SchoolApiRepository
    private val schoolsApi = mockk<SchoolApi>(relaxed = true)
    private val schoolsDatabaseRepo = mockk<IDbRepository>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        schoolsRepo = SchoolApiRepository(schoolsApi, schoolsDatabaseRepo, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
        clearAllMocks()
    }

    @Test
    fun `schoolRequestState StateFlow must be in a SUCCESS state`() {

        assertThat(schoolsRepo.schoolRequestState.value)
            .isInstanceOf(LOADING::class.java)

        assertThat(schoolsRepo.detailsRequestState.value)
            .isInstanceOf(LOADING::class.java)
    }

    @Test
    fun `refreshSchools should emit an ERROR state if the request to the api fails`() =
        runTest {

            schoolsRepo.refreshSchools()

            val subject = schoolsRepo.schoolRequestState.first()
            assertThat(subject)
                .isInstanceOf(ERROR::class.java)

            val error = subject as ERROR
            assertThat(error.throwable.message).isEqualTo("Not Success")
        }

    @Test
    fun `refreshSchools should emit an ERROR state if the schools response body is null`() =
        runTest {

            coEvery { schoolsApi.getSchools() } returns mockk {
                every { isSuccessful } returns true
                every { body() } returns null
            }

            coEvery { schoolsApi.getDetails() } returns mockk {
                every { isSuccessful } returns true
            }

            schoolsRepo.refreshSchools()

            val subject = schoolsRepo.schoolRequestState.first()
            assertThat(subject)
                .isInstanceOf(ERROR::class.java)

            val error = subject as ERROR
            assertThat(error.throwable.message).isEqualTo("Schools body is empty")
        }

    @Test
    fun `refreshSchools should emit an ERROR state if the schools details response body is null`() =
        runTest {

            coEvery { schoolsApi.getSchools() } returns mockk {
                every { isSuccessful } returns true
                every { body() } returns mockk()
            }

            coEvery { schoolsApi.getDetails() } returns mockk {
                every { isSuccessful } returns true
                every { body() } returns null
            }

            schoolsRepo.refreshSchools()

            val subject = schoolsRepo.schoolRequestState.first()
            assertThat(subject)
                .isInstanceOf(ERROR::class.java)

            val error = subject as ERROR
            assertThat(error.throwable.message).isEqualTo("School details body is empty")
        }

    @Test
    fun `refreshSchools should emit a SUCCESS state if both requests are good`() =
        runTest {

            coEvery { schoolsApi.getSchools() } returns mockk {
                every { isSuccessful } returns true
                every { body() } returns mockk()
            }

            coEvery { schoolsApi.getDetails() } returns mockk {
                every { isSuccessful } returns true
                every { body() } returns mockk()
            }

            schoolsRepo.refreshSchools()

            val subject = schoolsRepo.schoolRequestState.first()
            assertThat(subject)
                .isInstanceOf(SUCCESS::class.java)
        }

    @Test
    fun `after calling getSchoolDetails detailsRequestState must be in a LOADING state`() =
        runTest {
            val dbn = "21K360"
            coEvery { schoolsDatabaseRepo.details(dbn) } throws Exception()
            schoolsRepo.getSchoolDetails(dbn)

            assertThat(schoolsRepo.detailsRequestState.value)
                .isInstanceOf(LOADING::class.java)
        }

    @Test
    fun `after calling getSchoolDetails detailsRequestState must be in a SUCCESS state if dbn is found`() =
        runTest {
            val dbn = "21K360"
            coEvery { schoolsDatabaseRepo.details(dbn) } returns mockk()
            schoolsRepo.getSchoolDetails(dbn)

            assertThat(schoolsRepo.detailsRequestState.first())
                .isInstanceOf(SUCCESS::class.java)
        }

    @Test
    fun `after calling getSchoolDetails detailsRequestState must be in a ERROR state if dbn is not found`() =
        runTest {
            val dbn = "21K360"
            coEvery { schoolsDatabaseRepo.details(dbn) } returns null
            schoolsRepo.getSchoolDetails(dbn)

            val subject = schoolsRepo.detailsRequestState.first()
            assertThat(subject)
                .isInstanceOf(ERROR::class.java)

            val error = subject as ERROR
            assertThat(error.throwable.message).isEqualTo("No records Found")
        }
}