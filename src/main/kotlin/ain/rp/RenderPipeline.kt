package ain.rp

import ain.mesh.Mesh
import ain.mesh.MeshFactory
import ain.shader.Shader

abstract class RenderPipeline(protected val shader: Shader, val meshFactory: MeshFactory) {
    abstract fun render(obj: Renderable, mesh: Mesh)
}