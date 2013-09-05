CFLAGS := --std=c++11 -Wall
RELEASE := -O2
DEBUG := -g

asciidivs2html: asciidivs2html.cpp
	g++ $(CFLAGS) $(RELEASE) -Wall $< -o $@


all: asciidivs2html


.PHONY : clean

clean:
	@echo "Cleaning executable files" 
	rm -rf asciidivs2html
