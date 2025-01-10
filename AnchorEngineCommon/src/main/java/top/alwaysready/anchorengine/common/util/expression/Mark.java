package top.alwaysready.anchorengine.common.util.expression;

public abstract class Mark {
    public static final Mark UNKNOWN = new Mark(0) {
        @Override
        public void calc(CalcMemory mem) {

        }
    };
    public static final Mark LBR = new Mark(0) {
        @Override
        public void calc(CalcMemory mem) {

        }
    };
    public static final Mark RBR = new Mark(0) {
        @Override
        public void calc(CalcMemory mem) {

        }
    };
    public static final Mark ADD = new Mark(1) {
        @Override
        public void calc(CalcMemory mem) {
            mem.push(mem.pop() + mem.pop());
        }
    };
    public static final Mark SUB = new Mark(1) {
        @Override
        public void calc(CalcMemory mem) {
            mem.push(-mem.pop() + mem.pop());
        }
    };
    public static final Mark REV = new Mark(2) {
        @Override
        public void calc(CalcMemory mem) {
            mem.push(-mem.pop());
        }
    };
    public static final Mark MUL = new Mark(2) {
        @Override
        public void calc(CalcMemory mem) {
            mem.push(mem.pop() * mem.pop());
        }
    };
    public static final Mark DIV = new Mark(2) {
        @Override
        public void calc(CalcMemory mem) {
            double div = mem.pop();
            mem.push(mem.pop() / div);
        }
    };
    public static final Mark DIV_INT = new Mark(2) {
        @Override
        public void calc(CalcMemory mem) {
            double div = mem.pop();
            mem.push(Math.floor(mem.pop() / div));
        }
    };
    public static final Mark MOD = new Mark(2) {
        @Override
        public void calc(CalcMemory mem) {
            double n2 = mem.pop();
            double n1 = mem.pop();
            mem.push(n1 - Math.floor(n1 / n2) * n2);
        }
    };
    public static final Mark POW = new Mark(3) {
        @Override
        public void calc(CalcMemory mem) {
            double pow = mem.pop();
            mem.push(Math.pow(mem.pop(), pow));
        }
    };

    public static Mark number(double value) {
        return new NumberMark(value);
    }

    public static Mark var(String str) {
        return new VarMark("%"+str+"%");
    }

    private final int tier;

    public abstract void calc(CalcMemory mem);

    protected Mark(int tier) {
        this.tier = tier;
    }

    public static Mark of(char ch) {
        return switch (ch) {
            case '%' -> MOD;
            case '*' -> MUL;
            case '+' -> ADD;
            case '-' -> SUB;
            case '/' -> DIV;
            case '\\' -> DIV_INT;
            case '^' -> POW;
            case '(' -> LBR;
            case ')' -> RBR;
            default -> UNKNOWN;
        };
    }

    public int getTier() {
        return tier;
    }

    public static class NumberMark extends Mark{
        private final double value;

        public NumberMark(double value) {
            super(4);
            this.value = value;
        }

        @Override
        public void calc(CalcMemory mem) {
            mem.push(value);
        }
    }

    public static class VarMark extends Mark{
        private final String key;

        protected VarMark(String key) {
            super(4);
            this.key = key;
        }

        @Override
        public void calc(CalcMemory mem) {
            mem.push(mem.getValue(key).orElse(0d));
        }
    }
}
