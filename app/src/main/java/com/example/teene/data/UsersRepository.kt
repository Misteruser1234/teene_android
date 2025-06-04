import com.example.teene.data.network.NoAuthApiService
import com.example.teene.domain.models.AuthorizeRequest
import com.example.teene.domain.models.AuthorizeResponse
import com.example.teene.domain.models.UserRequest
import com.example.teene.domain.models.UsersCreateResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UsersRepository(
    //    private val authorizedApiService: AuthorizedApiService,
    private val noAuthApiService: NoAuthApiService
)
{
    fun createUser(userRequest: UserRequest): Flow<Result<UsersCreateResponse>> = flow {
        try
        {
            // Make the API call (POST /users/create) using the public service
            val response = noAuthApiService.createUser(userRequest)
            if (response.isSuccessful)
            {
                emit(Result.success(response.body()!!))  // Emit success result
            }
            else
            {
                emit(Result.failure(Exception("Failed to create user: ${response.message()}")))  // Emit failure result
            }
        } catch (e: Exception)
        {
            emit(Result.failure(e))  // Emit failure in case of an exception
        }
    }

    fun loginUser(loginRequest: AuthorizeRequest): Flow<Result<AuthorizeResponse>> = flow {
        try
        {
            // Make the API call (POST /users/create) using the public service
            val response = noAuthApiService.authorize(loginRequest)
            if (response.isSuccessful)
            {
                emit(Result.success(response.body()!!))  // Emit success result
            }
            else
            {
                emit(Result.failure(Exception("Failed to login user: ${response.message()}")))  //
            // Emit failure result
            }
        } catch (e: Exception)
        {
            emit(Result.failure(e))  // Emit failure in case of an exception
        }


    }
}