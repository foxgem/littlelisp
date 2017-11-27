package littlelisp

import spock.lang.*


class LittleLispSpec extends Specification {

    def parser =  new LittleLispParser()
    def interpreter = new LittleLispInterpreter(parser)

    @Unroll
    def "parsing '#a', getting '#b'"() {
        expect:
        parser.parse(a) == b

        where:
        a                       |       b
        '"a"'                   |      [type:"literal", value:"a"]
        "1"                     |      [type:"literal", value:1]
        "1.1"                   |      [type:"literal", value:1.1]
        'b'                     |      [type:"identifier", value:"b"]
        "()"                    |      []
        '(1 "a")'               |      [[type:"literal", value:1],[type:"literal", value:"a"]]
        '(1 c)'                 |      [[type:"literal", value:1],[type:"identifier", value:"c"]]
        "(1 1)"                 |      [[type:"literal", value:1],[type:"literal", value:1]]
        "(())"                  |      [[]]
        '(1 ("a") 1)'           |      [[type:"literal", value:1],[[type:"literal", value:"a"]],[type:"literal", value:1]]
        '(1 ("a" 1) 1)'         |      [[type:"literal", value:1],[[type:"literal", value:"a"],[type:"literal", value:1]],[type:"literal", value:1]]
        '(1 ("a" (1)) 1)'       |      [[type:"literal", value:1],[[type:"literal", value:"a"],[[type:"literal", value:1]]],[type:"literal", value:1]]
        '(1    2)'              |      [[type:"literal", value:1],[type:"literal", value:2]]
    }

    @Unroll
    def "evaluating '#a', getting '#b'"() {
        expect:
        interpreter.eval(a) == b

        where:
        a                       |       b
        "1"                     |       1
        '"c"'                   |       "c"
        "(print 1)"             |       1
        '(print "a")'           |       "a"
        '(first (1 2 3))'       |       1
        '(rest (1 2 "3"))'      |       [2,"3"]
        '(first (("3" 2) 1))'   |       ["3",2]
        '(rest (("3" 2) 1))'    |       [1]
        """((lambda (x)
              x)
            "lisp")"""          |       "lisp"
        """((lambda (x y)
              (x y))
            "lisp" 1)"""        |       ["lisp", 1]
        """((lambda (x y)
              (0 x y))
            "lisp" 1)"""        |       [0, "lisp", 1]
        """((lambda (x)
              (first(x)))
            "lisp")"""          |       "lisp"
        """((lambda ()
              (rest(1 2))))"""  |       [2]
        """(let ((x 1) (y 2))
              (x y))"""         |       [1,2]
        "(if 1 42 4711)"        |       42
        "(if 0 42 4711)"        |       4711
    }

}
