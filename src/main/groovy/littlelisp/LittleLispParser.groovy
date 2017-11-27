package littlelisp

class LittleLispParser {

    def parse(input){
        buildAST(tokenize(input), [])[0]
    }

    private List tokenize(statement){
        return statement.replaceAll(/\(/, " ( ").replaceAll(/\)/, " ) ").trim().split(/\s+/)
    }

    private Map categorize(token){
        if(token.isNumber()){
            return [
                type: "literal",
                value: new BigDecimal(token)
            ]
        } else if(token[0]=='"' && token[-1]=='"'){
            return [
                type: "literal",
                value: "${token[1..-2]}"
            ]
        }

        return [
            type: "identifier",
            value: token
        ]
    }

    private def buildAST(tokens, tree){
        //println "$tokens:$tree"

        if(tokens.empty) return tree
        def token = tokens.remove(0)

        if(token == "("){
            tree << buildAST(tokens, [])
            buildAST(tokens, tree)
        } else if(token == ")"){
            return tree
        } else{
            return buildAST(tokens, tree << categorize(token))
        }
    }

}
