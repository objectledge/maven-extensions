package org.objectledge.maven.connectors.jflex.tests;

%%

%public
%class SampleScanner
%unicode
%integer
%pack

%{
  public static final int LOWER = 0;
    
  public static final int UPPER = 1;
%}

%%

[:lowercase:]+ { return LOWER; }

[:uppercase:]+ { return UPPER; }

.|\n           { throw new Error("Illegal character <"+yytext()+">"); }

