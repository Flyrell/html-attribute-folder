package com.github.flyrell.htmlattributefolder.services

import com.intellij.openapi.project.Project
import com.github.flyrell.htmlattributefolder.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
