package ru.castprograms.calendarkmmsuai.repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ru.castprograms.calendarkmmsuai.data.Lesson
import ru.castprograms.calendarkmmsuai.data.Semester
import ru.castprograms.calendarkmmsuai.util.Resource
import ru.castprograms.calendarkmmsuai.data.Group

class TimeTableService {
    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
    private val baseUrl = "https://api.guap.ru/rasp/custom"

    suspend fun getSemInfo(): Resource<Semester> {
        return try {
            Resource.Success(
                Json.decodeFromString(
                    httpClient.get("$baseUrl/get-sem-info").body()
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    suspend fun getGroups(): Resource<List<Group>> {
        return try {
            Resource.Success(
                Json.decodeFromString(
                    httpClient.get("$baseUrl/get-sem_groups").body()
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    suspend fun getTimeTableGroup(numberGroup: String): Resource<Map<Int, List<Lesson>>> {
        return try {
            Resource.Success(
                (Json.decodeFromString(
                    httpClient.get("$baseUrl/get-sem-rasp/group$numberGroup").body()
                ) as List<Lesson>).sortedBy { it.less }.groupBy { it.week * 10 + it.day }
            )
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }
}