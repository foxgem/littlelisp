package littlelisp

class LittleLispInterpreter {

    def parser
    def lib
    def special = [
        lambda: { input, context ->
            return {
                def args = it
                def lambdaContext = new Context(context)

                //println args

                if(args instanceof List){
                    input[1].eachWithIndex { val, index ->
                        lambdaContext.put(val.value, args[index])
                    }
                }else{
                    if(input[1]){
                        lambdaContext.put(input[1][0].value, args)
                    }
                }
                interpret(input[2], lambdaContext)
            }
        },
        let: { input, context ->
            def letContext = new Context(context)
            input[1].each{
                letContext.put(it[0].value, it[1].value)
            }
            interpret(input[2], letContext)
        },
        "if": { input, context ->
            (interpret(input[1], context))? interpret(input[2], context):interpret(input[3], context)
        }
    ]

    class Context {
        Context parent
        Map scope = [:]

        def Context(parent){
            this.parent = parent
        }

        def put(key, value){
            scope."$key" = value
        }

        def get(key){
            if(!scope."$key"){
                if(parent){
                    return parent.get(key)
                }
                return null
            } else{
                return scope."$key"
            }
        }
    }

    def LittleLispInterpreter(parser){
        this.parser = parser
        this.lib = new Context(null)
        this.lib.scope = [
            print: {
                println it
                return it
            },
            first: {
                return it[0]
            },
            rest: {
                return it[1..-1]
            }
        ]
    }

    def eval(input){
        return interpret(parser.parse(input), new Context(lib))
    }

    private def interpret(input, context){
        //println input

        if(input instanceof List){
            return interpretList(input, context)
        } else{
            if(input.type == "literal"){
                return input.value
            } else{
                return context.get(input.value)
            }
        }
    }

    private def interpretList(input, context){
        if (special."${input[0].value}") {
            return special."${input[0].value}".call(input, context)
        } else {
            def list = input.collect{ interpret(it, context) }
            if(list[0] instanceof Closure){
                // check if there are parameters for the invocation
                def args = (list.size()==1)? []:list[1..-1]
                //println args
                if(args.size()==1){
                    list[0].call(args[0])
                }else{
                    list[0].call(args)
                }
            }else{
                return list
            }
        }
    }

}
