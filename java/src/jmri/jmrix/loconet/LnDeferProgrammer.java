package jmri.jmrix.loconet;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.ArrayList;

import jmri.ProgListener;
import jmri.Programmer;
import jmri.ProgrammerException;
import jmri.ProgrammingMode;

import jmri.jmrix.AbstractProgrammer;

/**
 * Programmer implementation for Programmer that uses a SlotManager (which is also an AbstractProgrammer)
 * that might be provided later. This is done by connecting through a LocoNetSystemConnectionMemo.
 *
 * @author Bob Jacobsen Copyright (C) 2018
 */
public class LnDeferProgrammer extends AbstractProgrammer {

    public LnDeferProgrammer(@Nonnull LocoNetSystemConnectionMemo memo) {
        this.memo = memo;
    }
    
    LocoNetSystemConnectionMemo memo;
    
    @Nonnull public List<ProgrammingMode> getSupportedModes() {
        SlotManager m = memo.getSlotManager();
        if (m!=null) {
            return m.getSupportedModes();
        } else {
            log.warn("getSupportedModes() called without a SlotManager");
            return new ArrayList<ProgrammingMode>(); // empty
        }
    }
        
    protected void timeout() {
        SlotManager m = memo.getSlotManager();
        if (m!=null) {
            m.timeout();
        } else {
            log.warn("timeout called without a SlotManager");
        }
    }
    
    public void writeCV(int CV, int val, ProgListener p) throws ProgrammerException {
        SlotManager m = memo.getSlotManager();
        if (m!=null) {
            m.writeCV(CV, val, p);
        } else {
            log.warn("writeCV called without a SlotManager");
        }
    }

    public void readCV(int CV, ProgListener p) throws ProgrammerException {
         SlotManager m = memo.getSlotManager();
        if (m!=null) {
            m.readCV(CV, p);
        } else {
            log.warn("readCV called without a SlotManager");
        }
   }

    public void confirmCV(String CV, int val, ProgListener p) throws ProgrammerException {
        SlotManager m = memo.getSlotManager();
        if (m!=null) {
            m.confirmCV(CV, val, p);
        } else {
            log.warn("confirmCV called without a SlotManager");
        }
    }

    // initialize logging
    private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SlotManager.class);

}
