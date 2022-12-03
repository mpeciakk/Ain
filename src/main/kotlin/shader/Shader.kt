package shader

import Destroyable
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20.*
import kotlin.system.exitProcess

abstract class Shader(shader: String) : Destroyable {
    private var program: Int
    private var vertex: Int
    private var fragment: Int
    private val locationCache: MutableMap<String, Int> = HashMap()
    private var matrixBuffer = BufferUtils.createFloatBuffer(16)

    init {
        val glslPreprocessor = GLSLPreprocessor()

        vertex = loadShader(
            """
            #version 400 core
            #define VERTEX
            ${glslPreprocessor.process(shader)}
            """.trim(), GL_VERTEX_SHADER
        )
        fragment = loadShader(
            """
            #version 400 core
            #define FRAGMENT
            ${glslPreprocessor.process(shader)}
            """.trim(), GL_FRAGMENT_SHADER
        )

        program = glCreateProgram()

        glAttachShader(program, vertex);
        glAttachShader(program, fragment);
        bindAttributes();
        glLinkProgram(program);
        glValidateProgram(program);

//        program = gl.createProgram()
//        glError("createProgram")
//        program.attach(vertex)
//        program.attach(fragment)
//        glError("attach")
//        bindAttributes()
//        glError("bindAttributes")
//        program.link()
//        glError("link")
//        program.validate()
//        glError("validate")


    }

    private fun loadShader(source: String, type: Int): Int {
        val shader = glCreateShader(type)

//        shader.source(source)
//        shader.compile()

        glShaderSource(shader, source);
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            println(glGetShaderInfoLog(shader, 500));
            println("Could not compile shader!");
            exitProcess(-1);
        }

        return shader
    }

    fun start() {
//        program.use()
        glUseProgram(program)
    }

    fun stop() {
//        program.unuse()
        glUseProgram(0)
    }

    private fun getUniformLocation(name: String): Int {
        return if (locationCache.containsKey(name)) {
            locationCache[name]!!
        } else {
            val location = glGetUniformLocation(program, name)
            locationCache[name] = location
            location
        }
    }

    fun loadTransformationMatrix(matrix: Matrix4f) {
//        loadMatrix("transformationMatrix", matrix)
    }

    fun loadProjectionMatrix(matrix: Matrix4f) {
//        loadMatrix("projectionMatrix", matrix)
    }

    fun loadViewMatrix(matrix: Matrix4f) {
//        loadMatrix("viewMatrix", matrix)
    }

//    fun loadFloat(name: String, value: Float) {
//        program.programUniform(getUniformLocation(name), value)
//    }
//
//    fun loadInt(name: String, value: Int) {
//        program.programUniform(getUniformLocation(name), value)
//    }
//
//    fun loadVector(name: String, vector: Vector3f) {
//        program.programUniform(getUniformLocation(name), vector.x, vector.y, vector.z)
//    }
//
//    fun loadVector(name: String, vector: Vector3i) {
//        program.programUniform(getUniformLocation(name), vector.x, vector.y, vector.z)
//    }
//
//    fun loadBoolean(name: String, value: Boolean) {
//        program.programUniform(getUniformLocation(name), if (value) 1 else 0)
//    }
//
//    fun loadMatrix(name: String, matrix: Mat4d) {
////        matrixBuffer = matrix.get(matrixBuffer)
//        program.programUniform(getUniformLocation(name), matrix)
////        program.program
////        matrixBuffer.clear()
//    }

    protected abstract fun bindAttributes()

    protected fun bindAttribute(attribute: Int, name: String) {
        glBindAttribLocation(program, attribute, name)
    }

    override fun destroy() {
        stop()
//        program.detach(vertex)
//        program.detach(fragment)
//        vertex.delete()
//        fragment.delete()
//        program.delete()
    }
}