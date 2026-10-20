package de.example.backend.service.ai.tool;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.ai.tool.annotation.*;


public class UnitTools {
    public enum Unit {
        MILLIGRAM,
        GRAM,
        KILOGRAM,
        OUNCE,
        POUND
    }
 
    private static final Map<Unit, Float> weightTable = new EnumMap<>(Map.of(
        Unit.MILLIGRAM, 0.001f,
        Unit.GRAM,      1f,
        Unit.KILOGRAM,  1000f,
        Unit.OUNCE,     28.349523f,
        Unit.POUND,     453.59237f
    ));

    @Tool(description = "Convert between weight units.")
    public float convertWeight(
            @ToolParam(description = "Amount of the source unit.") float amount,
            @ToolParam(description = "Source unit.") Unit from,
            @ToolParam(description = "Target unit to convert to.") Unit to) {
        return amount * weightTable.get(from) / weightTable.get(to);
    }
}
