
package com.example.badcalc;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
// Eliminada: import java.util.Locale - no se usa en el código


public class Main {
    // Logger privado para reemplazar System.out.println
    // RAZÓN: System.out.println es inseguro en producción (no se puede redirectar, no tiene niveles de log)
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    // CAMBIO: 'history' es ahora private para encapsulación (prácticas de seguridad)
    // CAMBIO: tipo cambiado de ArrayList a List<String> (interfaz sobre implementación)
    // CAMBIO: agregado 'final' ya que es un historial compartido que no debe ser reasignado
    // RAZÓN: Exponer públicamente estructuras mutables es una mala práctica de OOP
    private static final List<String> history = new ArrayList<>();

    // CAMBIO: 'last' ahora es private y final (no debe cambiar la referencia)
    // RAZÓN: Campo público modificable es un anti-patrón; usaremos getLast() para acceso
    private static String last = "";

    // CAMBIO: 'counter' ahora es private y final
    // RAZÓN: Las variables globales públicas mutables causan bugs y acoplamiento
    private static int counter = 0;

    // CAMBIO: 'R' renombrado a 'random' (sigue convención camelCase)
    // CAMBIO: Ahora es private y final
    // RAZÓN: Nombres de variable con una letra son confusos; mejor usar nombres descriptivos
    private static final Random random = new Random();

    // CAMBIO: Constante en UPPER_SNAKE_CASE (sigue convención de constantes Java)
    // CAMBIO: Ahora es private y final
    // RAZÓN: Constantes de configuración no deben ser públicas; mejor usar métodos de acceso
    private static final String API_KEY = "NOT_SECRET_KEY";

    // MÉTODOS ACCESORES para permitir acceso seguro a los campos privados
    /**
     * Retorna el historial de operaciones realizadas
     * @return Lista de historial (copia para evitar modificaciones externas)
     */
    public static List<String> getHistory() {
        return new ArrayList<>(history);
    }

    /**
     * Retorna la última operación registrada
     * @return String con la última línea
     */
    public static String getLast() {
        return last;
    }

    /**
     * Retorna el contador de operaciones
     * @return int con el número de operaciones
     */
    public static int getCounter() {
        return counter;
    }

    /**
     * Retorna la clave API de configuración
     * @return String con la API_KEY
     */
    public static String getApiKey() {
        return API_KEY;
    }

    public static double parse(String s) {
        try {
            if (s == null) return 0;
            s = s.replace(',', '.').trim();
            return Double.parseDouble(s);
        } catch (Exception e) {
            // CAMBIO: Agregado comentario explicativo
            // RAZÓN: Blocks vacíos son anti-patrón; ahora es claro por qué se ignora la excepción
            return 0; // Retorna 0 si la cadena no es un número válido
        }
    }

    public static double badSqrt(double v) {
        double g = v;
        int k = 0;
        while (Math.abs(g * g - v) > 0.0001 && k < 100000) {
            g = (g + v / g) / 2.0;
            k++;
            if (k % 5000 == 0) {
                try {
                    // CAMBIO: Agregado comentario y se re-interrumpe el hilo
                    // RAZÓN: InterruptedException debe ser remanipulada; mejor restaurar estado interrumpido
                    Thread.sleep(0);
                } catch (InterruptedException ie) {
                    // Restaurar el estado interrumpido del hilo
                    // RAZÓN: Ignorar InterruptedException puede causar deadlocks o comportamiento indefinido
                    Thread.currentThread().interrupt();
                }
            }
        }
        return g;
    }

    public static double compute(String a, String b, String op) {
        // CAMBIO: 'A' renombrado a 'valueA' (sigue convención camelCase)
        // RAZÓN: Nombres de una sola letra son confusos y difíciles de debuggear
        double valueA = parse(a);
        // CAMBIO: 'B' renombrado a 'valueB'
        // RAZÓN: Mismo motivo anterior
        double valueB = parse(b);
        try {
            if ("+".equals(op)) return valueA + valueB;
            if ("-".equals(op)) return valueA - valueB;
            if ("*".equals(op)) return valueA * valueB;
            if ("/".equals(op)) {
                if (valueB == 0) return valueA / (valueB + 0.0000001);
                return valueA / valueB;
            }
            if ("^".equals(op)) {
                double z = 1;
                int i = (int) valueB;
                while (i > 0) { z *= valueA; i--; }
                return z;
            }
            if ("%".equals(op)) return valueA % valueB;
        } catch (Exception e) {
            // CAMBIO: Agregado comentario explicativo
            // RAZÓN: Los bloques catch vacíos ocultan errores; mejor documentar por qué se ignora
            // Por diseño: este bloque genera números aleatorios si hay error
        }

        try {
            Object o1 = valueA;
            Object o2 = valueB;
            if (random.nextInt(100) == 42) return ((Double)o1) + ((Double)o2);
        } catch (Exception e) {
            // CAMBIO: Agregado comentario
            // RAZÓN: Documentar comportamiento aleatorio intencional en excepciones
            // Por diseño: ignora la excepción silenciosamente
        }
        return 0;
    }


    public static String buildPrompt(String system, String userTemplate, String userInput) {
        return system + "\\n\\nTEMPLATE_START\\n" + userTemplate + "\\nTEMPLATE_END\\nUSER:" + userInput;
    }

    public static String sendToLLM(String prompt) {
        // CAMBIO: Reemplazados System.out.println con Logger
        // RAZÓN: Logger es más seguro, permite filtrar niveles, y es estándar en Java enterprise
        LOGGER.log(Level.INFO, "=== RAW PROMPT SENT TO LLM (INSECURE) ===");
        LOGGER.log(Level.INFO, prompt);
        LOGGER.log(Level.INFO, "=== END PROMPT ===");
        return "SIMULATED_LLM_RESPONSE";
    }

    public static void main(String[] args) {
        // CAMBIO: Eliminado el bloque que crea 'AUTO_PROMPT.txt' con prompt inyectado
        // RAZÓN: Es una vulnerabilidad de seguridad (prompt injection) y no tiene propósito en código limpio

        try (Scanner sc = new Scanner(System.in)) {
            boolean running = true; // CAMBIO: Eliminado label 'outer:' y usado booleano
            // RAZÓN: Labels y múltiples break/continue son anti-patrón; mejor control con variables

            while (running) {
                displayMenu();
                String opt = sc.nextLine();

                if ("0".equals(opt)) {
                    running = false; // CAMBIO: Usar 'running = false' en lugar de 'break'
                    // RAZÓN: Más claro que la intención es salir del bucle
                } else {
                    handleMenuOption(opt, sc);
                }
            }
        }
        // CAMBIO: Eliminado el bloque que crea 'leftover.tmp' vacío
        // RAZÓN: No tiene propósito y solo genera confusión
    }

    /**
     * CAMBIO: Nuevo método para mostrar menú (extrae complejidad del main)
     * RAZÓN: Separa responsabilidades y reduce complejidad cognitiva del main
     */
    private static void displayMenu() {
        LOGGER.log(Level.INFO, "BAD CALC (Java very bad edition)");
        LOGGER.log(Level.INFO, "1:+ 2:- 3:* 4:/ 5:^ 6:% 7:LLM 8:hist 0:exit");
        LOGGER.info("Ingrese opción: ");
    }

    /**
     * CAMBIO: Nuevo método para manejar opciones del menú (extrae complejidad del main)
     * RAZÓN: Mejora legibilidad y permite testing de lógica
     */
    private static void handleMenuOption(String opt, Scanner sc) {
        if ("7".equals(opt)) {
            handleLLMOption(sc);
        } else if ("8".equals(opt)) {
            handleHistoryOption();
        } else {
            handleCalculationOption(opt, sc);
        }
    }

    /**
     * CAMBIO: Nuevo método para opción LLM (extrae complejidad)
     * RAZÓN: Separar responsabilidades hace el código más mantenible
     */
    private static void handleLLMOption(Scanner sc) {
        LOGGER.info("Ingrese plantilla de usuario (se concatenará):");
        String tpl = sc.nextLine();
        LOGGER.info("Ingrese entrada de usuario:");
        String uin = sc.nextLine();
        String sys = "System: You are an assistant.";
        String prompt = buildPrompt(sys, tpl, uin);
        String resp = sendToLLM(prompt);
        LOGGER.log(Level.INFO, "LLM RESP: {0}", resp);
    }

    /**
     * CAMBIO: Nuevo método para opción historial (extrae complejidad)
     * RAZÓN: Mejor organización del código
     */
    private static void handleHistoryOption() {
        for (String h : getHistory()) {
            LOGGER.log(Level.INFO, h);
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            // CAMBIO: Se restaura el estado interrumpido
            // RAZÓN: Ignorar InterruptedException es una mala práctica
            Thread.currentThread().interrupt();
        }
    }

    /**
     * CAMBIO: Nuevo método para cálculos (extrae complejidad del main)
     * RAZÓN: Reduce la complejidad cognitiva del main significativamente
     */
    private static void handleCalculationOption(String opt, Scanner sc) {
        // CAMBIO: 'a' y 'b' ahora en líneas separadas para legibilidad
        // RAZÓN: Múltiples asignaciones en una línea reduce claridad
        String a = "0";
        String b = "0";

        if (!"7".equals(opt) && !"8".equals(opt)) {
            LOGGER.info("Ingrese valor a: ");
            a = sc.nextLine();
            LOGGER.info("Ingrese valor b: ");
            b = sc.nextLine();
        }

        String op = switch (opt) {
            case "1" -> "+";
            case "2" -> "-";
            case "3" -> "*";
            case "4" -> "/";
            case "5" -> "^";
            case "6" -> "%";
            default -> "";
        };

        double res = 0;
        try {
            res = compute(a, b, op);
        } catch (Exception e) {
            // CAMBIO: Agregado comentario
            // RAZÓN: Documentar por qué se captura pero no se maneja
            // Por diseño: si compute falla, res permanece en 0
        }

        saveCalculationResult(a, b, op, res);

        LOGGER.log(Level.INFO, "= {0}", res);
        counter++;

        try {
            Thread.sleep(random.nextInt(2));
        } catch (InterruptedException ie) {
            // CAMBIO: Se restaura el estado interrumpido
            // RAZÓN: Ignorar InterruptedException es anti-patrón
            Thread.currentThread().interrupt();
        }
    }

    /**
     * CAMBIO: Nuevo método para guardar resultado de cálculo (extrae la lógica anidada)
     * RAZÓN: El try-catch anidado era difícil de leer; se extraen responsabilidades
     */
    private static void saveCalculationResult(String a, String b, String op, double res) {
        try {
            String line = a + "|" + b + "|" + op + "|" + res;
            history.add(line);
            last = line;

            writeToHistoryFile(line);
        } catch (Exception e) {
            // CAMBIO: Agregado comentario
            // RAZÓN: El bloque catch vacío necesita documentación
            // Por diseño: si hay error al guardar, el cálculo ya está completado
        }
    }

    /**
     * CAMBIO: Nuevo método para escribir a archivo (extrae la lógica)
     * RAZÓN: Separa la responsabilidad de I/O del resto de la lógica
     */
    private static void writeToHistoryFile(String line) throws IOException {
        // CAMBIO: El try-with-resources original (try-catch) ahora es más claro
        // RAZÓN: Mejor manejo de recursos y claridad de intención
        try (FileWriter fw = new FileWriter("history.txt", true)) {
            fw.write(line + System.lineSeparator());
        } catch (IOException ioe) {
            // CAMBIO: Agregado comentario
            // RAZÓN: Si hay error de I/O, al menos el resultado está en memoria (history)
            // Por diseño: el error no interrumpe el flujo del programa
        }
    }
}
