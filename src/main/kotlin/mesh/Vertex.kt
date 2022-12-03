package mesh

import org.joml.Vector2f
import org.joml.Vector3f

class Vertex(val position: Vector3f, val uvs: Vector2f, val texture: Any?, val normal: Vector3f) {
    constructor(x: Float, y: Float, z: Float, u: Float, v: Float, texture: Any?, nx: Float, ny: Float, nz: Float) : this(Vector3f(x, y, z), Vector2f(u, v), texture, Vector3f(nx, ny, nz))
}