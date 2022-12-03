import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryStack

class Window(val width: Int, val height: Int, title: String) : Destroyable {
    private val id: Long

    var shouldClose: Boolean
        get() = glfwWindowShouldClose(id)
        set(value) = glfwSetWindowShouldClose(id, value)

    init {
        GLFWErrorCallback.createPrint(System.err).set()

        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        id = glfwCreateWindow(width, height, title, 0, 0)
    }

    fun create() {
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        if (id == 0L) {
            throw RuntimeException("Failed to create GLFW window!")
        }

        MemoryStack.stackPush().use { stack ->
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)
            glfwGetWindowSize(id, pWidth, pHeight)
            val videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor())!!
            glfwSetWindowPos(
                id,
                (videoMode.width() - pWidth.get(0)) / 2,
                (videoMode.height() - pHeight.get(0)) / 2
            )
        }

        glfwMakeContextCurrent(id)
        glfwShowWindow(id)

        GL.createCapabilities()
    }

    fun update() {
        glfwSwapBuffers(id)
        glfwPollEvents()
    }

    override fun destroy() {
        glfwDestroyWindow(id)
        glfwTerminate()
    }
}