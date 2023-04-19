# AutoAnimate
[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.aghajari/AutoAnimate.svg?label=Maven%20Central)](https://search.maven.org/artifact/io.github.aghajari/AutoAnimate/1.0.2/aar)
[![Join the chat at https://gitter.im/Aghajari/community](https://badges.gitter.im/Aghajari/community.svg)](https://gitter.im/Aghajari/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

**AutoAnimate** is a custom Shared Element Transition that automatically animates your views based on Figma Smart Animate.

| FragmentA | FragmentB | Transition |
| :---: | :---: | :---: |
| <img src="./images/fragment_a.png" width=200 title="Layout"> | <img src="./images/fragment_b.png" width=200 title="Layout"> | <img src="./images/result.gif" width=200 title="Transition"> |

**Supports:**
- [x] Layout
- [x] Scale
- [x] Rotation
- [x] Translation
- [x] Background
- [x] Color
- [x] TextSize
- [x] TextColor
- [x] CornerRadius
- [x] Elevation

You can customize duration, delay, interpolator and every other properties!

## Installation

**AutoAnimate** is available in the `mavenCentral()`, so you just need to add it as a dependency (Module gradle)

Gradle
```gradle
implementation 'io.github.aghajari:AutoAnimate:1.0.2'
```

Maven
```xml
<dependency>
  <groupId>io.github.aghajari</groupId>
  <artifactId>AutoAnimate</artifactId>
  <version>1.0.2</version>
  <type>pom</type>
</dependency>
```

## Usage

- Step1: Create your fragments (or activities)
- Step2: Design your layouts
- Step3: Set `sharedElementEnterTransition`
- Step4: Add your views to FragmentTransaction by `addSharedElement`
- Step5: Done!

```kotlin
val autoAnimateTransition = AutoAnimateTransition.build(
    duration = 500,
    interpolator = OvershootInterpolator()
)

fragment.sharedElementEnterTransition = autoAnimateTransition
sharedElementReturnTransition = autoAnimateTransition

supportFragmentManager
    .beginTransaction()
    .addToBackStack(null)
    .addSharedElement(view, view.transitionName)
    .replace(R.id.container, fragment)
    .commit()
```
