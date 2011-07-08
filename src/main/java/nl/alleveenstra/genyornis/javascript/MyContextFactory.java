package nl.alleveenstra.genyornis.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

/**
 * @author alle.veenstra@gmail.com
 */
class MyFactory extends ContextFactory
 {
     private boolean running = true;

     private static class MyContext extends Context
     {
         long startTime;
     }

     static {
         ContextFactory.initGlobal(new MyFactory());
     }

     @Override
     protected Context makeContext()
     {
         MyContext cx = new MyContext();
         cx.setInstructionObserverThreshold(10000);
         return cx;
     }

     // Override hasFeature(Context, int)
     public boolean hasFeature(Context cx, int featureIndex)
     {
         // Turn on maximum compatibility with MSIE scripts
         switch (featureIndex) {
             case Context.FEATURE_NON_ECMA_GET_YEAR:
                 return true;

             case Context.FEATURE_MEMBER_EXPR_AS_FUNCTION_NAME:
                 return true;

             case Context.FEATURE_RESERVED_KEYWORD_AS_IDENTIFIER:
                 return true;

             case Context.FEATURE_PARENT_PROTO_PROPERTIES:
                 return false;
         }
         return super.hasFeature(cx, featureIndex);
     }



     @Override
     protected void observeInstructionCount(Context cx, int instructionCount)
     {
         if (!running) {
             throw new Error();
         }
     }

     public void gracefullyQuit() {
         running = false;
     }
 }
