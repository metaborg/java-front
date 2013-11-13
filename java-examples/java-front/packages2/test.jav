package barker;

import Root;
import animals.barkable.*;
import animals.meowable.Cat;
import animals.meowable.Cat.Mouth;

class Barker {
	Cat cat1;
	animals.meowable.Cat cat2;
	meowable.Cat cat3; // Fail
	
	Mouth catmouth1;
	Cat.Mouth catmouth2;
	animals.meowable.Cat.Mouth catmouth3;
	meowable.Cat.Mouth catmouth4; // Fail
	
	Dog dog1;
	animals.barkable.Dog dog2;
	barkable.Dog dog3; // Fail
	
	Root root;
}



