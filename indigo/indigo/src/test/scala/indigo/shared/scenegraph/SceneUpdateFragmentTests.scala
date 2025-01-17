package indigo.shared.scenegraph

import indigo.shared.datatypes.BindingKey
import indigo.shared.datatypes.RGBA
import indigo.shared.materials.BlendMaterial

class SceneUpdateFragmentTests extends munit.FunSuite {

  test("Adding a layer with an existing key merges magnification down (none, none)") {

    val scene =
      SceneUpdateFragment.empty.addLayer(Layer(BindingKey("key A")))

    val actual =
      scene.addLayer(Layer(BindingKey("key A")))

    assert(actual.layers.length == 1)
    assertEquals(actual.layers.head.magnification, None)

  }

  test("Adding a layer with an existing key merges magnification down (some, some)") {

    val scene =
      SceneUpdateFragment.empty.addLayer(Layer(BindingKey("key A")).withMagnification(2))

    val actual =
      scene.addLayer(Layer(BindingKey("key A")).withMagnification(1))

    assert(actual.layers.length == 1)
    assertEquals(actual.layers.head.magnification, Some(2))

  }

  test("Adding a layer with an existing key merges magnification down (none, some)") {

    val scene =
      SceneUpdateFragment.empty.addLayer(Layer(BindingKey("key A")))

    val actual =
      scene.addLayer(Layer(BindingKey("key A")).withMagnification(1))

    assert(actual.layers.length == 1)
    assertEquals(actual.layers.head.magnification, Some(1))

  }

  test("Adding a layer with an existing key merges magnification down (some, none)") {

    val scene =
      SceneUpdateFragment.empty.addLayer(Layer(BindingKey("key A")).withMagnification(2))

    val actual =
      scene.addLayer(Layer(BindingKey("key A")))

    assert(actual.layers.length == 1)
    assertEquals(actual.layers.head.magnification, Some(2))

  }

  test("SUF append preseves layer keys") {

    val sceneA: SceneUpdateFragment =
      SceneUpdateFragment.empty.addLayer(Layer(BindingKey("key A")).withMagnification(2))

    val sceneB: SceneUpdateFragment =
      SceneUpdateFragment.empty.addLayer(Layer(BindingKey("key A")).withMagnification(3))

    val actual: SceneUpdateFragment =
      sceneA |+| sceneB

    assert(actual.layers.length == 1)
    assertEquals(actual.layers.head.magnification, Some(2))

  }

  test("Can add a blend material with no Blending instance in place") {
    val scene =
      SceneUpdateFragment.empty.withBlendMaterial(BlendMaterial.Lighting(RGBA.Red))

    scene.blendMaterial match
      case Some(BlendMaterial.Lighting(color)) =>
        assertEquals(color, RGBA.Red)

      case _ =>
        fail("match failed")
  }

  test("Can modify blending with no Blending instance in place") {
    val scene =
      SceneUpdateFragment.empty.modifyBlendMaterial(_ => BlendMaterial.Lighting(RGBA.Red))

    scene.blendMaterial match
      case Some(BlendMaterial.Lighting(color)) =>
        assertEquals(color, RGBA.Red)

      case _ =>
        fail("match failed")
  }

}
