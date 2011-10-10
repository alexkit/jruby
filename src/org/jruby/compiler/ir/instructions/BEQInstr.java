package org.jruby.compiler.ir.instructions;

import org.jruby.compiler.ir.Operation;
import org.jruby.compiler.ir.operands.Label;
import org.jruby.compiler.ir.operands.Operand;
import org.jruby.compiler.ir.operands.Nil;
import org.jruby.compiler.ir.operands.BooleanLiteral;
import org.jruby.compiler.ir.representations.InlinerInfo;
import org.jruby.interpreter.InterpreterContext;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

public class BEQInstr extends BranchInstr {
    public BEQInstr(Operand v1, Operand v2, Label jmpTarget) {
        super(Operation.BEQ, v1, v2, jmpTarget);
    }

    public Instr cloneForInlining(InlinerInfo ii) {
        return new BEQInstr(operand1.cloneForInlining(ii), operand2.cloneForInlining(ii), ii.getRenamedLabel(target));
    }

    @Override
    public Label interpret(InterpreterContext interp, ThreadContext context, IRubyObject self) {
        Operand op1 = getOperand1();
        Operand op2 = getOperand2();
        Object value1 = op1.retrieve(interp, context, self);
        if (op2 instanceof BooleanLiteral) {
            boolean v1True  = ((IRubyObject)value1).isTrue();
            boolean op2True = ((BooleanLiteral)op2).isTrue();
            return (v1True && op2True) || (!v1True && !op2True) ? target : null;
        }
        else {
            Object  value2 = op2.retrieve(interp, context, self);
            boolean eql    = (op2 == Nil.NIL) ? (value1 == value2) : ((IRubyObject)value1).op_equal(context, (IRubyObject)value2).isTrue();
            return eql ? target : null;
        }
    }
}
