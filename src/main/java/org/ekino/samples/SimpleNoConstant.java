// java --source 21 --enable-preview SimpleNoConstant.java 10
void even(){System.out.println("even");}
void odd(){System.out.println("odd");}

static long next = -1;
long next(){return ++next;}

void process0(long number) {
    if (number == next()) even();
    if (number == next()) odd();
    if (number == next()) even();
    if (number == next()) odd();
    if (number == next()) even();
}

void processX(long number) {
    if (number == next()) even();
    if (number == next()) odd();
    if (number == next()) even();
    if (number == next()) odd();
    if (number == next()) even();
}

void main(String[] a) {
    long number = Long.parseLong(a[0]);
    process0(number);
    processX(number);
    //(...)
}

