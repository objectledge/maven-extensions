/*
 * #########
 *  This is a sample script file, were we can check all features of FCKpackager.
 *  The code has not much sense and it's here for demonstration purposes only.
 *
 * Please note that all statements are correctly terminated with a semi-colon
 * (except brackets enclosed blocks, where it is optional). This is a
 * requirement, otherwise your packed code may be broken.
 * #########
 */

/*
 * These constants must not be deployed. They are used in the development
 * environment only, for readability and code comprehension.
 */
var BASIC_COLOR_RED = 1 ;
var BASIC_COLOR_BLUE = 2 ;
var BASIC_COLOR_WHITE = 3 ;

// Create the global "MyObject".
var MyObject = new Object() ;
MyObject.CONST = 1 ;

var _Html =
'<div>\
	<i>\
		Some text\
	</i>\
</div>' ;

// Get the color name.
MyObject.GetColorName = function( colorId, upperCase )
{
	var name ;

	if ( !colorId )
		colorId = MyObject.CONST ;

	switch ( colorId )
	{
		case BASIC_COLOR_RED :
			name = 'Red' ;
			break;

		case BASIC_COLOR_BLUE :
			name = 'Blue' ;
			break;

		case BASIC_COLOR_WHITE :
			name = 'White' ;
			break;

		default :
			// The following must strings must be combined.
			name =
				'Unknown ' +
				'color ' +
				'id' +
				" (double quotes " + "too)" ;
	}

	return upperCase ? name.toUpperCase() : name ;
};

// The following function has not much sense... it is just an example.
MyObject.GetArray = function( value1, value2, value3, theLastWellDescriptiveArgument )
{
	var myArray = new Array() ;

	var A = 'Let\'s see if the name "A" will cause conflict' ;

	// Let's just push the arguments in the array.
	myArray.push( value1 ) ;
	myArray.push( value2 ) ;
	myArray.push( value3 ) ;
	myArray.push( theLastWellDescriptiveArgument ) ;

	// The following line must not be deployed.
	MyDebugFunction( myArray ) ;		// @Packager.RemoveLine

	return myArray ;
};

// @Packager.Remove.Start
// This function is used during the development. We don't want to deploy it,
// so we use the @Packager.Remove.(Start/End) instructions.
function DebugIt( message )
{
	alert( message ) ;
}
// @Packager.Remove.End
