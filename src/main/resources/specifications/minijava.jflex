package phase.b_syntax;
%% 
%include Jflex.include
%include JflexCup.include

/// Macros
WS         = [ \t\f] | \R
EOLComment = "//" .*
C89Comment = "/*" [^*]* ("*" ([^*/] [^*]*)?)* "*/"
Ignore     = {WS} | {EOLComment} | {C89Comment}
Integer    = 0 | [1-9] [0-9]*
Boolean    = "true" | "false"
Ident      = [:jletter:] [:jletterdigit:]*

%%
//// Mots Clés
"boolean" { return TOKEN(BOOLEAN); }
"class"   { return TOKEN(CLASS);   }
"else"    { return TOKEN(ELSE);    } 
"extends" { return TOKEN(EXTENDS); }
"if"      { return TOKEN(IF);      }
"int"     { return TOKEN(INT);     }
"length"  { return TOKEN(LENGTH);  }
"main"    { return TOKEN(MAIN);    }
"new"     { return TOKEN(NEW);     }
"out"     { return TOKEN(OUT);     }
"println" { return TOKEN(PRINTLN); }
"public"  { return TOKEN(PUBLIC);  }
"return"  { return TOKEN(RETURN);  }
"static"  { return TOKEN(STATIC);  }
"String"  { return TOKEN(STRING);  }
"System"  { return TOKEN(SYSTEM);  }
"void"    { return TOKEN(VOID);    }
"while"   { return TOKEN(WHILE);   }
//// Operateurs
"&&"      { return TOKEN(AND);     }
"="       { return TOKEN(ASSIGN);  }
"<"       { return TOKEN(LESS);    }
"-"       { return TOKEN(MINUS);   }
"!"       { return TOKEN(NOT);     }
"+"       { return TOKEN(PLUS);    }
"*"       { return TOKEN(TIMES);   }
//// Ponctuations 
","       { return TOKEN(COMMA);   }
"."       { return TOKEN(DOT);     }
";"       { return TOKEN(SEMI);    }
"["       { return TOKEN(LBRACK);  }
"]"       { return TOKEN(RBRACK);  }
"{"       { return TOKEN(LBRACE);  }
"}"       { return TOKEN(RBRACE);  }
"("       { return TOKEN(LPAREN);  }
")"       { return TOKEN(RPAREN);  }
//// Literals, Identificateurs
{Integer} { return TOKEN(LIT_INT,  Integer.parseInt(yytext()));     }  
{Boolean} { return TOKEN(LIT_BOOL, Boolean.parseBoolean(yytext())); }
{Ident}   { return TOKEN(IDENT,    new String(yytext())) ;          }
//// Ignore, Ramasse Miette
{Ignore}  { }
[^]       { WARN("Invalid char '" + yytext() + "'"); return TOKEN(error); }
