SYNONYM DICTIONARY STORAGE PROTOCOL


1.  INTRODUCTION 

Our protocol lets a client search the server for a word and all of its synonyms that have been defined by the client. The client communicates with the server using a GUI to access the port number specified by the server. The server can be run through the command prompt. The client can also connect to the server via the command prompt by giving the correct IP address and port number. The server accepts multiple client connections. Every time a client connects to the server, it will create a new thread to run the client commands.

2. SYNONYM DICTIONARY

When the server starts the dictionary is empty. There are no words with synonyms stored. In order to add words to the dictionary the client will need to type the SET command followed by a pair of words that will be synonyms. When the server receives this command it will take the two words, check if either of them are in the dictionary already. If the words are not in the dictionary, the server will create a group of synonyms that will include those two words. If one word is in the dictionary and the other is not the server will add the new word into the synonym group of the existing word. If both words are in the dictionary then the server will combine both groups that those two words are part of. If the server receives a GET request with a single word, the server will send the client that word with a list of all the synonyms previously specified by the client. The client can also send a REMOVE command with one word. This removes the given word from the dictionary if it is there.

3. FORMAT FOR COMMANDS


3.1 CLIENT

The client will need to follow these formats to send messages through the GET and SET buttons:


SET

	SET word1 word2 (words are separated by a space) 

	Example:

	SET thanks obama


GET

	GET word (one word, searches dictionary for that word and synonyms)

	Example:

	GET obama 


REMOVE

	REM word (one word, searches dictionary for it and its synonyms)

	Example:

	REM obama


3.2 SERVER

The server will return the following messages after each command:


SET

	Notifies the user if the words are saved into the dictionary.


GET

	Gives the user back the specified word and all of its synonyms.


REMOVE

	Notifies the user that the word has been removed if it was there in the first place.


4. SYNCRONIZATION

Adding and removing words from and to a dictionary in different orders would be able to cause a race condition in some instances. For this reason we will only allow one client to modify the dictionary with one command at a time across all clients. When that command is complete the others will be able to proceed. We will use semaphores to maintain this critical area.

5. ERRORS

When the client sends the server a bad command the server will send an error message back to the client. There are different types of errors. Firstly not using the specified format the server will send back a format error message. An error specific to the GET and REMOVE commands is if the specified word is not in the dictionary. The server will send back a nonexistent word request error message. The GUI will let the user know what type of error they are getting by displaying the message. Another error the client will receive from itself is if it specifies an incorrect IP address and port number.
