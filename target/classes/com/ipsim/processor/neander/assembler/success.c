
#include <life.h>
//Algorithm of Success

int main() {

    int success = 0;
    int dead = 0;

    while(!dead) {

        success = tryAgain();
        if(success) {

            improve();
        }
        dead = isLifeOver();
    }
    return 0;
}