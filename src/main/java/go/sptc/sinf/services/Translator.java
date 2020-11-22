package go.sptc.sinf.services;

public class Translator {

    public static String translateCatFolder(String value) {
        switch (value) {
            case "Text Documents":
                return "Documentos de texto";
            case "PDF Documents":
                return "Documentos PDF";
            case "HTML Documents":
                return "Documentos HTML";
            case "RTF Documents":
                return "Documentos RTF";
            case "Other Documents":
                return "Outros documentos";
            case "XML Files":
                return "Arquivos XML";
            case "Databases":
                return "Banco de dados";
            case "Spreadsheets":
                return "Planilhas";
            case "Presentations":
                return "Apresentações";
            case "Contacts":
                return "Contatos";
            case "Chats":
                return "Bate-papos";
            case "Internet History":
                return "Históricos de internet";
            case "Link files":
                return "Atalhos";
            case "URL links":
                return "Atalhos de URL";
            case "Temporary Internet Texts":
                return "Texto temporários de internet";
            case "Texts in System Folders":
                return "Arquivos de textos em pastas de sistema";
            case "Other Texts":
                return "Outros textos";
            case "Scanned Documents":
                return "Documentos escaneados";
            case "Temporary Internet Images":
                return "Imagens temporárias de internet";
            case "Images in System Folders":
                return "Imagens em pastas de sistemas";
            case "Other Images":
                return "Outras imagens";
            case "Videos":
                return "Vídeos";
            case "Audios":
                return "Audios";
            case "OLE files":
                return "Arquivos OLE";
            case "Compressed Archives":
                return "Arquivos comprimidos";
            case "Mailboxes":
                return "Caixa de email";
            case "Windows Registry":
                return "Registro do Windows";
            case "Programs and Libraries":
                return "Programas e bibliotecas";
            case "Unallocated":
                return "Espaço não alocado";
            case "Other files":
                return "Outros arquivos";
            case "Empty Files":
                return "Arquivos vazios";
        }
        return value;
    }

}
