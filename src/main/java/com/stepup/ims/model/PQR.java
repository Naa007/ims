package com.stepup.ims.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class PQR {

    private Long id;
    private List<Skills> skills;
    private Education education;
    private EnglishSkills englishSkills;
    private List<ProfessionalQualifications> professionalQualifications;
    private Experience experience;
    private int score;
    private List<OtherProfessionalSkills> otherProfessionalSkills;
    private String remarks;
    private Long inspectorId;



    @Getter
    @AllArgsConstructor
    public enum Skills {
        PLATES("Plates"), COILS("Coils"), WIRES("Wires"), BARS("Bars"), SECTIONS_ISMB_ISMC_ISA("Sections - ISMB, ISMC, ISA"), PIPES("Pipes"), TUBES("Tubes"), PIPE_FITTINGS("Pipe Fittings"), FLANGES("Flanges"), FASTENERS("Fasteners"), GASKETS("Gaskets"), LINE_PIPES("Line Pipes"), CASTINGS("Castings"), FORGINGS("Forgings"), SHAFT("shaft"), INSTRUMENTATION_FITTINGS("Instrumentation fittings"), FILTER("Filter"), CLINKER_CRUSHER("Clnker crusher"), REACTOR("reactor"), PRESSURE_VESSELS("Pressure Vessels"), CLADDED_VESSELS("Cladded Vessels"), AIR_RECEIVERS("Air Receivers"), BOILER("Boiler"), DEAERATORS("Deaerators"), STEAM_DRUM("Steam Drum"), WHRB("WHRB"), STORAGE_TANKS("Storage Tanks"), HEAT_EXCHANGER("Heat Exchanger"), STEEL_STRUCTURE("Steel Structure"), LP_HEATERS("LP Heaters"), HP_HEATER("HP Heater"), SUPER_HEATERS("Super Heaters"), ECONOMIZER("Economizer"), PROCESS_EQUIPMENT("Process equipment"), STACKER("stacker"), CHIMNEY("chimney"), SILO("Silo"), HOPPER("Hopper"), HYDRAULIC("Hydraulic"), CONVEYOR("Conveyor"), MANUAL_VALVES("Manual vales"), PNEUMATIC_OPERATED_VALVES("Pnuematic operated Valves"), ELECTRICAL_OPERATED_VALVES("Electrical operated valves"), PUMPS("Pumps"), FANS_BLOWERS_DRYERS("Fans, Blowers, Dryers"), CRANES("Cranes"), GEAR_BOXES("Gear Boxes"), CENTRIFUGES("Centrifuges"), COMPRESSORS("Compressors"), TURBINES("Turbines"), DIESEL_GENERATOR("Diesel Generator"), MATERIAL_HANDLING_EQUIPMENTS("Material Handling Equipments"), PORT_HANDLING_EQUIPMENTS("Port Handling Equipments"), COUPLINGS("Couplings"), BEARINGS_SEALS("Bearings, Seals"), PULVERIZERS("Pulverizers"), EXPANSION_BELLOWS("Expansion Bellows"), BLAST_FURNACE("Blast Furnace"), STEEL_MILL("Steel Mill"), BALL_MILL("Ball Mill"), CEMENT_AND_PHARMA_EQUIPMENTS("Cement & Pharma Equipments"), TRANSFORMERS("Transformers"), UPS("UPS"), CABLE_HV_MV_LV_CONT_INST("Cable(HV,MV,LV,Cont, Inst.)"), UPS_BATTERY("UPS BATTERY"), SWITCHGEAR("Switchgear"), IPB_NSPB_BUS_DUCT("IPB, NSPB, Bus Duct"), CIRCUIT_BREAKERS("Circuit Breakers"), MOTOR("Motor"), MCC_PLC_PANEL_DB("MCC, PLC Panel , DB"), SOLAR_CELLS_MODULES_PANELS("Solar cells, Modules, panels"), GENERATORS("Generators"), CONVERTORS("Convertors"), ACCUMULATORS("Accumulators"), INVERTERS("Inverters"), CCTV("CCTV"), SCADA("SCADA"), JUNCTION_BOX("Junction Box"), ARRESTORS("Arrestors"), CONNECTORS("Connectors"), INSULATION("Insulation"), FLOW_ELEMENT_LEVEL_GAUGE("Flow Element & Level Gauge"), PRESSURE_GAUGES("Pressure Guages"), TRANSMITTERS("Transmitters"), THERMO_COUPLES("Thermo Couples"), THERMOWELLS("Thermowells"), FLOW_METERS("Flow Meters"), ROTAMETER("Rotameter"), AVO_METERS("AVO Meters"), PRESSURE_SAFETY_VALVE("Pressure Safety Valve"), PRESSURE_RELIEF_VALVE("Pressure Relief Valve");
        @Getter
        private final String name;
    }


    @Getter
    @AllArgsConstructor
    public enum Education {
        POST_GRADUATION("Post Graduate (Engineering/Science)", "1", "10"), ENGINEERING("4-Years Engineering Degree", "0.8", "10"), DIPLOMA("3 Year Diploma in Engineering", "0.8", "10"), UNIVERSITY("3 Years University Degree (Science)", "0.5", "10"), HIGH_SCHOOL("High School (Technical)", "0.25", "10");

        @Getter
        private final String qualification;
        private final String rating;
        private final String factor;
    }

    @Getter
    @AllArgsConstructor
    public enum EnglishSkills {
        FLUENT("Fluent", "1", "5"), INTERMEDIATE("Intermediate", "0.5", "5");

        @Getter
        private final String name;
        @Getter
        private final String rating;
        @Getter
        private final String factor;

    }

    @Getter
    public enum ProfessionalQualifications {
        AWS("AWS CWI/CSWIP/IWE/IWT", "1", "5"), ASNT("ASNT NDT level II/PCN Level II", "1", "5"), NACE("NACE Level II/BGAS Gr II", "1", "5");

        @Getter
        private final String name;
        private final String rating;
        private final String factor;

        ProfessionalQualifications(String name, String rating, String factor) {
            this.name = name;
            this.rating = rating;
            this.factor = factor;
        }

    }

    @Getter
    public enum Experience {
        OVER25("Over 25 years", "1", "20"), BETWEEN2025("20~24.99 years", "0.8", "20"), BETWEEN1519("15~19.99 years", "0.6", "20"), BETWEEN1014("10~14.99 years", "0.5", "20"), BETWEEN59("5~9.99 years", "0.4", "20"), LESS5("0~4.99 years", "0.0", "20");

        @Getter
        private final String name;
        private final String rating;
        private final String factor;

        Experience(String name, String rating, String factor) {
            this.name = name;
            this.rating = rating;
            this.factor = factor;
        }

    }

    @Getter
    public enum OtherProfessionalSkills {
        ASME("ASME Authorized Inspector"), ARAMCO("ARAMCO"), API("API"), NDT("NDT Level III");

        @Getter
        private final String name;

        OtherProfessionalSkills(String name) {
            this.name = name;
        }

    }
}
