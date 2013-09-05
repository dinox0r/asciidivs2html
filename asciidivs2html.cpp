#include <fstream>
#include <iostream>
#include <string>
#include <vector>
#include <algorithm>
#include <utility>
#include <climits>
#include <cstdlib>

#include <getopt.h>

//////////////////////////////////////////////////////////////////
// STRUCT AND CLASSES 
//////////////////////////////////////////////////////////////////

inline int clamp(int value, int minValue, int maxValue);

struct Div {
    char id;
    int x0, y0;
    int xf, yf;
    std::vector<Div*> children;
    
    Div(char id) : id(id), x0(INT_MAX), y0(INT_MAX), xf(INT_MIN), yf(INT_MIN) {}
    Div(char id, int x0, int y0, int xf, int yf) : id(id), x0(x0), y0(y0), xf(xf), yf(yf) {}
    
    char searchEnclosingDiv(std::vector< std::vector<char> >& visited, int w, int h) const {
        int offsetX[] = { x0 - 1, x0    , xf + 1, xf };
        int offsetY[] = { y0    , y0 - 1, yf    , yf + 1 };
        for (int i = 0; i < 4; i++) { 
            int x = offsetX[i];
            int y = offsetY[i];
            
            clamp(x, 0, w);
            clamp(y, 0, h);
            
            if (visited[x][y] != ' ') return visited[x][y];
        }
        return ' ';
    }
};

std::ostream& operator<<(std::ostream& out, const Div& r) {
    return out << "{ id = " << r.id << ", coordinates = [" << r.x0 << ", " << r.y0 << "], " << "[" << r.xf << ", " << r.yf << "] }";
}

class AsciiImage {
    size_t w;
    size_t h;
    char horizontalWall;
    char verticalWall;
    std::vector<std::string> lines;
    
    struct AsciiImageLine {
        const int x;
        const std::vector<std::string>& lines;
        // We get a reference of the lines vector and the user given x coordinate
        // so we can later get the pixel on X,Y when the operator [] is called for this struct
        AsciiImageLine(const int x, const std::vector<std::string>& lines) : x(x), lines(lines) { }

        const char operator[](int y) const { 
            // Clamp the y coordinate
            int ySize = static_cast<int>(lines.size());
            y = clamp(y, 0, ySize);

            // The y coordinate represents a line index in the lines vector
            const std::string line = lines[y];

            // Clamp the x coordinate
            int xSize = static_cast<int>(line.size());
            const int nx = clamp(x, 0, xSize);

            // We have been pass the x coordinate, which is the index of a character in the selected line
            return line[nx];
        }
    };
public:
    AsciiImage() : w(0), h(0), horizontalWall('-'), verticalWall('I') {}
                
    void addLine(const std::string& line) {
        if (line.size() > w) { 
            w = line.size();
        }
        lines.push_back(line);
        h++;
    }

    char getVerticalWall() const { return verticalWall; }
    
    void setVerticalWall(char verticalWall) { 
        this->verticalWall = verticalWall;
    }
    
    char getHorizontalWall() const { return horizontalWall; }    
    
    void setHorizontalWall(char horizontalWall) { 
        this->horizontalWall = horizontalWall;
    }
    
    size_t width() const { return w; }

    size_t height() const { return h; }    

    const AsciiImageLine operator[](int x) const { 
        return AsciiImageLine(x, lines);
    }
    
    void loadAsciiImageFile(const std::string& asciiImageFilePath) {
        std::ifstream fis(asciiImageFilePath);

        if (!fis.is_open()) {
            std::cerr << "Couldn't open file '" << asciiImageFilePath << "'" << std::endl;
        } else {
            std::string line;
            while(std::getline(fis, line)) {
                addLine(line);
            }
        }
    }
};


//////////////////////////////////////////////////////////////////
// TYPEDEF'S
//////////////////////////////////////////////////////////////////
typedef Div Rect;


//////////////////////////////////////////////////////////////////
// UTILITY FUNCTIONS
//////////////////////////////////////////////////////////////////
static std::string getFileName(int argc, char* argv[]) {
    // Option parsing    
    struct option options[] = {
        {"file", required_argument, nullptr, 'f'},
        // For the function getopt_long the array must end with a structure containing all zeros
        {0,0,0,0}
    };        
    std::string fileName = "ascii.in";
    char opt;
    while ((opt = getopt_long(argc, argv, "f:", options, nullptr)) != -1) {
        if (opt == 'f') {
            fileName = optarg;
        }
    }    
    return fileName;
}

inline int clamp(int value, int minValue, int maxValue) {
    return value < minValue ? minValue : (value >= maxValue ? maxValue - 1 : value); 
}

// Taken from: https://gist.github.com/h3nr1x/6207450
template<typename Type> 
std::vector< std::vector<Type> > createTable(size_t numRows, size_t numCols, Type defaultValue = Type()) { 
    std::vector< std::vector<Type> > table;
    for (size_t i = 0; i < numRows; ++i) { 
        table.push_back(std::vector<Type>(numCols, defaultValue));
    }
    return table;
}


//////////////////////////////////////////////////////////////////
// PROBLEM RELATED FUNCTIONS
//////////////////////////////////////////////////////////////////
void floodFill(const AsciiImage& img, int x, int y, Rect& bestRect, std::vector< std::vector<char> >& visited) {
    if (x < 0 || x >= static_cast<int>(img.width()) || y < 0 || y >= static_cast<int>(img.height())) {
        return;
    }
    
    if (visited[x][y] != ' ') {
        // Element already visited
        return;
    }
    
    const char VERTICAL_WALL = img.getVerticalWall();
    const char HORIZONTAL_WALL = img.getHorizontalWall();
    
    bool recurseLeft = true;
    bool recurseRight = true;
    bool recurseTop = true;
    bool recurseBottom = true;
    
    if (img[x + 1][y] == VERTICAL_WALL) {
        if (x + 1 > bestRect.xf) {
            bestRect.xf = x + 1;
        }
        recurseRight = false;
    }
    if (img[x + 1][y] == HORIZONTAL_WALL) recurseRight = false;

    if (img[x - 1][y] == VERTICAL_WALL) {
        if (x - 1 < bestRect.x0) {
            bestRect.x0 = x - 1;
        }
        recurseLeft = false;
    }
    if (img[x - 1][y] == HORIZONTAL_WALL) recurseLeft = false;    

    if (img[x][y - 1] == HORIZONTAL_WALL) {
        if (y - 1 < bestRect.y0) {
            bestRect.y0 = y - 1;
        }
        recurseTop = false;
    }        
    if (img[x][y - 1] == VERTICAL_WALL) recurseTop = false;    
    
    if (img[x][y + 1] == HORIZONTAL_WALL) {
        if (y + 1 > bestRect.yf) {
            bestRect.yf = y + 1;
        }
        recurseBottom = false;
    }
    if (img[x][y + 1] == VERTICAL_WALL) recurseBottom = false;    

    visited[x][y] = bestRect.id;    
    
    if (recurseLeft)   floodFill(img, x - 1, y, bestRect, visited);
    if (recurseRight)  floodFill(img, x + 1, y, bestRect, visited);
    if (recurseTop)    floodFill(img, x, y - 1, bestRect, visited);
    if (recurseBottom) floodFill(img, x, y + 1, bestRect, visited);    
}

Div* findDivById(char id, std::vector<Div>& divs) {
    for (Div& div : divs) {
        if (div.id == id) { 
            return &div;
        }
    }
    return nullptr;
}

void printNode(const Div* node, const std::string& tab = "") {
    std::cout << tab << "<div>" << std::endl;
    if (node->id != ' ') {
        std::cout << tab << tab << node->id << std::endl;
    }
    for (auto child : node->children) {
        printNode(child, tab + "   ");
    }
    std::cout << tab << "</div>" << std::endl;    
}

int main(int argc, char* argv[]) { 
    std::string fileName = getFileName(argc, argv);

    AsciiImage img;
    img.loadAsciiImageFile(fileName);
    
    size_t w = img.width();
    size_t h = img.height();    
    
    if (w == 0 || h == 0) {
        std::cerr << "Couldn't load properly" << std::endl;
        exit(1);
    }
    
    std::vector< std::pair<int, int> > notWallChars;

    const char EMPTY_SPACE = ' ';
    const char VERTICAL_WALL = 'I';
    const char HORIZONTAL_WALL = '-';
    const char wallCharacters[] = { VERTICAL_WALL, HORIZONTAL_WALL };
    
    img.setHorizontalWall(HORIZONTAL_WALL);
    img.setVerticalWall(VERTICAL_WALL);
    
    // First scan the image searching for those non wall characters
    for (size_t y = 0; y < h; y++) { 
        for (size_t x = 0; x < w; x++) {
            char asciiPixel = img[x][y];
            if (asciiPixel == EMPTY_SPACE) { 
                continue;
            }
            
            bool isWallChar = false;
            for (char c : wallCharacters) {
                if (asciiPixel == c) {
                    isWallChar = true;
                    break;
                }
            }
            if (!isWallChar) {
                notWallChars.push_back(std::make_pair(x, y));       
            }
        }
    }

    std::vector< std::vector<char> > visited = createTable<char>(w, h, ' ');
    
    std::vector<Div> divs;
    for (auto xy : notWallChars) {
        char id = img[xy.first][xy.second];
        
        Div bestDiv(id);
        floodFill(img, xy.first, xy.second, bestDiv, visited);
        
        divs.push_back(bestDiv);
    }

    Div root(' ', 0, 0, static_cast<int>(w), static_cast<int>(h));

    for (Div& div : divs) {
        char idOfParent = div.searchEnclosingDiv(visited, w, h);

        // Check out the borders of the div
        if (idOfParent != ' ') {
            Div* parentDiv = findDivById(idOfParent, divs);
            parentDiv->children.push_back(&div);
        } else {
            root.children.push_back(&div);
        }
    }

    // Traverse the tree with the nodes
    printNode(&root);
    
    return 0;
}