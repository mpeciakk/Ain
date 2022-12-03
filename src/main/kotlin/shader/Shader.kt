package shader

import Destroyable
import glm_.mat4x4.Mat4d
import gln.ShaderType
import gln.gl
import gln.identifiers.GlProgram
import gln.identifiers.GlShader
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector3i
import org.lwjgl.BufferUtils
import kotlin.system.exitProcess

abstract class Shader(shader: String) : Destroyable {
    private var program: GlProgram
    private var vertex: GlShader
    private var fragment: GlShader
    private val locationCache: MutableMap<String, Int> = HashMap()
    private var matrixBuffer = BufferUtils.createFloatBuffer(16)

    init {
        val glslPreprocessor = GLSLPreprocessor()

        vertex = loadShader(
            """
            #version 400 core
            #define VERTEX
            ${glslPreprocessor.process(shader)}
            """.trim(), ShaderType.VERTEX_SHADER
        )
        fragment = loadShader(
            """
            #version 400 core
            #define FRAGMENT
            ${glslPreprocessor.process(shader)}
            """.trim(), ShaderType.FRAGMENT_SHADER
        )

        program = gl.createProgram()
        program.attach(vertex)
        program.attach(fragment)
        bindAttributes()
        program.link()
        program.validate()
    }

    private fun loadShader(source: String, type: ShaderType): GlShader {
        val shader = gl.createShader(type)

        shader.source(source)
        shader.compile()

        if (!shader.compileStatus) {
            println("Could not compile shader!")
            println(shader.infoLog)
            exitProcess(-1)
        }

        return shader
    }

    fun start() {
        program.use()
    }

    fun stop() {
        program.unuse()
    }

    private fun getUniformLocation(name: String): Int {
        return if (locationCache.containsKey(name)) {
            locationCache[name]!!
        } else {
            val location = program.getUniformLocation(name)
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

    fun loadFloat(name: String, value: Float) {
        program.programUniform(getUniformLocation(name), value)
    }

    fun loadInt(name: String, value: Int) {
        program.programUniform(getUniformLocation(name), value)
    }

    fun loadVector(name: String, vector: Vector3f) {
        program.programUniform(getUniformLocation(name), vector.x, vector.y, vector.z)
    }

    fun loadVector(name: String, vector: Vector3i) {
        program.programUniform(getUniformLocation(name), vector.x, vector.y, vector.z)
    }

    fun loadBoolean(name: String, value: Boolean) {
        program.programUniform(getUniformLocation(name), if (value) 1 else 0)
    }

    fun loadMatrix(name: String, matrix: Mat4d) {
//        matrixBuffer = matrix.get(matrixBuffer)
        program.programUniform(getUniformLocation(name), matrix)
//        program.program
//        matrixBuffer.clear()
    }

    protected abstract fun bindAttributes()

    protected fun bindAttribute(attribute: Int, name: String) {
        program.bindAttribLocation(attribute, name)
    }

    override fun destroy() {
        stop()
        program.detach(vertex)
        program.detach(fragment)
        vertex.delete()
        fragment.delete()
        program.delete()
    }
}