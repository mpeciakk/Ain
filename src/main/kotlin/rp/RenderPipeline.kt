package rp

import mesh.Mesh
import mesh.MeshFactory
import shader.Shader

abstract class RenderPipeline(protected val shader: Shader, val meshFactory: MeshFactory) {
    abstract fun render(mesh: Mesh)
}