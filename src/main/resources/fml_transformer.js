function initializeCoreMod() {
    return {
        'test': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraftforge.network.HandshakeHandler',
                'methodName': 'handleServerModListOnClient',
                'methodDesc': '(Lnet/minecraftforge/network/HandshakeMessages$S2CModList;Ljava/util/function/Supplier;)V'
            },
            'transformer': function(method) {
                print('[Mod Blocker Transformer]: Patching HandshakeMessages$S2CModList');

                var owner = "de/canitzp/modblocker/ModBlocker";
                var name = "transform";
                var desc = "(Lnet/minecraftforge/network/HandshakeMessages$S2CModList;)Z";
                var instr = method.instructions;

                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

                var i, j = -1;
                for(i = 0; i < instr.size(); i++){
                    if(j >= 0){
                        j++;
                    }
                    var instruction = instr.get(i);
                    if(j === 2){
                        instr.insertBefore(instruction, new VarInsnNode(Opcodes.ALOAD, 1));
                        instr.insertBefore(instruction, ASMAPI.buildMethodCall(
                            owner,
                            name,
                            desc,
                            ASMAPI.MethodType.STATIC));
                        instr.insertBefore(instruction, new VarInsnNode(Opcodes.ISTORE, 3));
                        print('[Mod Blocker Transformer]: Patching successful');
                        break;
                    }
                    if(instruction.getOpcode() === Opcodes.INVOKESTATIC){
                        if(instruction.name === "validateServerChannels"){
                            j = 0;
                        }
                    }
                }
                return method;
            }
        }
    }
}