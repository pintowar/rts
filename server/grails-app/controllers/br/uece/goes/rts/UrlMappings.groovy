package br.uece.goes.rts

class UrlMappings {

    static mappings = {
        delete "/$controller/$id(.$format)?"(action: "delete")
        get "/$controller(.$format)?"(action: "index")
//        get "/$controller/$id(.$format)?"(action: "show")
//        post "/$controller(.$format)?"(action: "save")
//        put "/$controller/$id(.$format)?"(action: "update")
//        patch "/$controller/$id(.$format)?"(action: "patch")

        "/"(controller: 'application', action: 'index')
        "500"(view: '/error')
        "404"(view: '/notFound')

//        get "/task/channel(.$format)?"(controller: 'task', action: "channel")
        get "/task/start-solver(.$format)?"(controller: 'task', action: "startSolver")
        get "/task/stop-solver(.$format)?"(controller: 'task', action: "stopSolver")
        get "/task/solutions(.$format)?"(controller: 'task', action: "solutions")
    }
}
