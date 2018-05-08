package org.trustnote.db;

import org.trustnote.db.entity.Inputs;
import org.trustnote.db.entity.Outputs;
import org.trustnote.db.entity.TBaseEntity;

import java.util.ArrayList;
import java.util.List;

public class Payload extends TBaseEntity {
    public List<Inputs> inputs = new ArrayList<>();
    public List<Outputs> outputs = new ArrayList<>();
}
