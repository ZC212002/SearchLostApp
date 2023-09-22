import java.io.Serializable

data class Task(
    var taskId: Int,
    var taskTitle: String,
    var taskName: String,
    var taskPhotoUrl: String?,
    var taskDateTime: String?

) : Serializable
