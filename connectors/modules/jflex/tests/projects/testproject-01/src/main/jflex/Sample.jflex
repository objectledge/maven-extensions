/*
 * Copyright (c) 2011 Caltha - Krzewski, Mach, Potempski Sp. J.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Caltha - Krzewski, Mach, Potempski Sp. J.
 */
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

