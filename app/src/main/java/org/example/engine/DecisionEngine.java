package org.example.engine;

import org.example.domain.DecisionRequest;
import org.example.domain.DecisionResult;
import java.util.List;

public interface DecisionEngine {
    List<DecisionResult> evaluate(DecisionRequest request);
}