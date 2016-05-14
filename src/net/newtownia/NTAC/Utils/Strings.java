package net.newtownia.NTAC.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vinc0682 on 09.05.2016.
 */
public class Strings
{
    private static List<String> strings;

    public static String getString(int index, int key)
    {
        key = 3 * (4 - key);
        if (strings == null)
        {
            strings = new ArrayList<>();
            strings.add("S2lsbGF1cmEtTlBD"); // 0: Killaura-NPC

            strings.add("Tm8gS25vY2tiYWNr"); // 1: No Knockback
            strings.add("QW50aUtub2NrYmFjaw=="); // 2: AntiKnockback
            strings.add("QW50aS1Lbm9ja2JhY2suQWRqdXN0bWVudA=="); // 3: Anti-Knockback.Adjustment
            strings.add("QW50aS1Lbm9ja2JhY2suS2ljay1NZXNzYWdl"); // 4: Anti-Knockback.Kick-Message
            strings.add("bnRhYy5ieXBhc3MuYW50aWtub2NrYmFjaw=="); // 5: ntac.bypass.antiknockback

            strings.add("JjQmbE5JQ0hUIEFOWklFSEVO"); // 6: &4&lNICHT ANZIEHEN
            strings.add("QXV0by1Bcm1vcg=="); // 7: Auto-Armor
            strings.add("bnRhYy5ieXBhc3MuYXV0b2FybW9y"); // 8: ntac.bypass.autoarmor
            strings.add("QXV0by1Bcm1vci5DaGVjay1GcmVxdWVuY3k="); // 9: Auto-Armor.Check-Frequency
            strings.add("QXV0by1Bcm1vci5DaGVjay1UaW1l"); // 10: Auto-Armor.Check-Time
            strings.add("QXV0by1Bcm1vci5JdGVtLU5hbWU="); // 11: Auto-Armor.Item-Name
            strings.add("QXV0by1Bcm1vci5BY3Rpb25z"); // 12: Auto-Armor.Actions
            key += 1;
        }
        key *= 5;
        String encrypted = strings.get(index);
        Charset charset = StandardCharsets.UTF_8;

        try
        {
            String classString = "java.util.Base64" + key;
            classString = classString.substring(0, 16);

            Class<?> base64 = Class.forName(classString);
            Method getDecoder = base64.getMethod("getDecoder", new Class[0]);

            Object decoder = getDecoder.invoke(null, null);
            Method decode = decoder.getClass().getMethod("decode", byte[].class);

            byte[] decoded = (byte[]) decode.invoke(decoder, encrypted.getBytes(charset));
            return new String(decoded, charset);
        } catch (ClassNotFoundException e) {
            return "Motherfucking error1";
        } catch (NoSuchMethodException e) {
            return "Motherfucking error2";
        } catch (InvocationTargetException e) {
            return "Motherfucking error3";
        } catch (IllegalAccessException e) {
            return "Motherfucking error4";
        }
        //return new String(Base64.getDecoder().decode(encrypted.getBytes(charset)));
    }
}
