package edu.ycp.cs340.jsonparser;

public class JSONParser {
	private Lexer lexer;
	
	public JSONParser(Lexer lexer) {
		this.lexer = lexer;
	}
	
	public Node parseValue() 
	{
		Node value = new Node(Symbol.VALUE);
		
		Token tok = lexer.peek();
		
		if(tok == null) {
			throw new ParserException("Unexpected end of input reading value");
		}
		
		if(tok.getSymbol() == Symbol.STRING_LITERAL) {
			// Value → StringLiteral
			value.getChildren().add(expect(Symbol.STRING_LITERAL));
		} else if (tok.getSymbol() == Symbol.INT_LITERAL) {
			// Value → IntLiteral
			value.getChildren().add(expect(Symbol.INT_LITERAL));
		} else if (tok.getSymbol() == Symbol.LBRACE) {
			// Value → Object
			value.getChildren().add(parseObject());
		} else if (tok.getSymbol() == Symbol.LBRACKET) {
			// Value → Array
			value.getChildren().add(parseArray());
		} else {
			throw new ParserException("Unexpected token looking for value: " + tok);
		}
		
		return value;
	}

	private Node parseObject() 
	{
		Node object = new Node(Symbol.OBJECT);
		
		// Object → "{" OptFieldList "}"
		object.getChildren().add(expect(Symbol.LBRACE));
		object.getChildren().add(parseOptFieldList());
		object.getChildren().add(expect(Symbol.RBRACE));
		
		return object;
	}
	
	private Node parseArray() 
	{
		Node array = new Node(Symbol.ARRAY);
		
		// Array → "[" OptValueList "]"
		array.getChildren().add(expect(Symbol.LBRACKET));
		array.getChildren().add(parseOptValueList());
		array.getChildren().add(expect(Symbol.RBRACKET));
		
		return array;
	}
	
	private Node parseOptFieldList() 
	{
		Node OptFieldList = new Node(Symbol.OPT_FIELD_LIST);
		
		Token tok = lexer.peek();
		
		if(tok.getSymbol() == Symbol.RBRACE) {
			// OptFieldList → ε "Do Nothing"
			return null;
		}
		// OptFieldList → FieldList → StringLiteral
		OptFieldList.getChildren().add(parseFieldList());
		return OptFieldList;
	}

	private Node parseOptValueList() 
	{
		Node OptValueList = new Node(Symbol.OPT_VALUE_LIST);
		
		Token tok = lexer.peek();
		
		if(tok.getSymbol() == Symbol.RBRACKET) {
			// OptValueList → ε "Do Nothing"
			return null;
		}
		// OptValueList → FieldList → StringLiteral
		OptValueList.getChildren().add(parseValueList());
		return OptValueList;
	}
	
	private Node parseFieldList() 
	{
		Node FieldList = new Node(Symbol.FIELD_LIST);
		
		parseField();
		Token tok = lexer.peek();
		
		if(tok.getSymbol() == Symbol.COMMA) {
			FieldList.getChildren().add(parseFieldList());
		}
		return FieldList;
	}
	
	private Node parseValueList() 
	{
		Node ValueList = new Node(Symbol.VALUE_LIST);
			
		parseField();
		Token tok = lexer.peek();
			
		if(tok.getSymbol() == Symbol.COMMA) {
				ValueList.getChildren().add(parseFieldList());
		}
		return ValueList;
	}
	
	
	private Node parseField()
	{
		Node field = new Node(Symbol.FIELD);
		
		field.getChildren().add(expect(Symbol.STRING_LITERAL));
		field.getChildren().add(expect(Symbol.COLON));
		field.getChildren().add(expect(Symbol.VALUE));
		
		return field;
	}
	

	private Node expect(Symbol symbol) {
		Token tok = lexer.next();
		if(tok.getSymbol() != symbol) {
			throw new LexerException("Unexpected token " + tok + " (was expecting " + symbol + ")");
		}
		return new Node(tok);
	}
}
