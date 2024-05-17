import {create, SetState} from 'zustand';

interface Extension {
    id: string;
    name: string;
    regex: string;
    required: boolean;
}

interface ExtensionInfoState {
    extensions: Extension[];
    setExtensions: (newSetExtension: Extension[]) => void;
    SA0001: number;
    TA0001: number;
    TA0002: number;
    TA0003: number;
    TA0004: number;
    TA0005: string;
    TA0006: boolean;
    TA0007: string;
    TA0008: number;
    TA0009: string;
    TA0013: boolean;
    TA0014: boolean;
    TA0015: boolean;
    TA0016: boolean;
    TA0017: boolean;
    TA0018: number;
    TA0019: number;
    TA0020: number;
    TA0021: number;
    TA0022: number;
    TA0023: string;
    TA0024: number;
    TA0025: number;
    TA0026: number;
    TA0027: string;
    TA0090: string;
    TA0091: string;
    TA0092: string;
    TA0093: string;
    TA0094: boolean;
    TA0095: string;
    setSA0001: (newSetText: number) => void;
    setTA0001: (newSetText: number) => void;
    setTA0002: (newSetText: number) => void;
    setTA0003: (newSetText: number) => void;
    setTA0004: (newSetText: number) => void;
    setTA0005: (newSetText: string) => void;
    setTA0006: (newSetText: boolean) => void;
    setTA0007: (newSetText: string) => void;
    setTA0008: (newSetText: number) => void;
    setTA0009: (newSetText: string) => void;
    setTA0013: (newSetText: boolean) => void;
    setTA0014: (newSetText: boolean) => void;
    setTA0015: (newSetText: boolean) => void;
    setTA0016: (newSetText: boolean) => void;
    setTA0017: (newSetText: boolean) => void;
    setTA0018: (newSetText: number) => void;
    setTA0019: (newSetText: number) => void;
    setTA0020: (newSetText: number) => void;
    setTA0021: (newSetText: number) => void;
    setTA0022: (newSetText: number) => void;
    setTA0023: (newSetText: string) => void;
    setTA0024: (newSetText: number) => void;
    setTA0025: (newSetText: number) => void;
    setTA0026: (newSetText: number) => void;
    setTA0027: (newSetText: string) => void;
    setTA0090: (newSetText: string) => void;
    setTA0091: (newSetText: string) => void;
    setTA0092: (newSetText: string) => void;
    setTA0093: (newSetText: string) => void;
    setTA0094: (newSetText: boolean) => void;
    setTA0095: (newSetText: string) => void;
}

const useExtensionInfoStore = create<ExtensionInfoState>((set: SetState<ExtensionInfoState>) => ({
    extensions: [],
    SA0001: 0,
    TA0001: 0,
    TA0002: 0,
    TA0003: 0,
    TA0004: 0,
    TA0005: "",
    TA0006: false,
    TA0007: "",
    TA0008: 0,
    TA0009: "",
    TA0013: false,
    TA0014: false,
    TA0015: false,
    TA0016: false,
    TA0017: false,
    TA0018: 0,
    TA0019: 0,
    TA0020: 0,
    TA0021: 0,
    TA0022: 0,
    TA0023: "",
    TA0024: 0,
    TA0025: 0,
    TA0026: 0,
    TA0027: "",
    TA0090: "",
    TA0091: "",
    TA0092: "",
    TA0093: "",
    TA0094: false,
    TA0095: "",
    setExtensions: (newSetExtension) => set({ extensions: newSetExtension}),
    setSA0001: (newSetText) => set({ SA0001: newSetText }),
    setTA0001: (newSetText) => set({ TA0001: newSetText }),
    setTA0002: (newSetText) => set({ TA0002: newSetText }),
    setTA0003: (newSetText) => set({ TA0003: newSetText }),
    setTA0004: (newSetText) => set({ TA0004: newSetText }),
    setTA0005: (newSetText) => set({ TA0005: newSetText }),
    setTA0006: (newSetText) => set({ TA0006: newSetText }),
    setTA0007: (newSetText) => set({ TA0007: newSetText }),
    setTA0008: (newSetText) => set({ TA0008: newSetText }),
    setTA0009: (newSetText) => set({ TA0009: newSetText }),
    setTA0013: (newSetText) => set({ TA0013: newSetText }),
    setTA0014: (newSetText) => set({ TA0014: newSetText }),
    setTA0015: (newSetText) => set({ TA0015: newSetText }),
    setTA0016: (newSetText) => set({ TA0016: newSetText }),
    setTA0017: (newSetText) => set({ TA0017: newSetText }),
    setTA0018: (newSetText) => set({ TA0018: newSetText }),
    setTA0019: (newSetText) => set({ TA0019: newSetText }),
    setTA0020: (newSetText) => set({ TA0020: newSetText }),
    setTA0021: (newSetText) => set({ TA0021: newSetText }),
    setTA0022: (newSetText) => set({ TA0022: newSetText }),
    setTA0023: (newSetText) => set({ TA0023: newSetText }),
    setTA0024: (newSetText) => set({ TA0024: newSetText }),
    setTA0025: (newSetText) => set({ TA0025: newSetText }),
    setTA0026: (newSetText) => set({ TA0026: newSetText }),
    setTA0027: (newSetText) => set({ TA0027: newSetText }),
    setTA0090: (newSetText) => set({ TA0090: newSetText }),
    setTA0091: (newSetText) => set({ TA0091: newSetText }),
    setTA0092: (newSetText) => set({ TA0092: newSetText }),
    setTA0093: (newSetText) => set({ TA0093: newSetText }),
    setTA0094: (newSetText) => set({ TA0094: newSetText }),
    setTA0095: (newSetText) => set({ TA0095: newSetText }),
}));

export const useExtensions = () => useExtensionInfoStore((state) => state.extensions);
export const useSetExtensions = () => useExtensionInfoStore((state) => state.setExtensions);

export const useSA0001 = () => useExtensionInfoStore((state) => state.SA0001);
export const useSetSA0001 = () => useExtensionInfoStore((state) => state.setSA0001);

export const useTA0001 = () => useExtensionInfoStore((state) => state.TA0001);
export const useSetTA0001 = () => useExtensionInfoStore((state) => state.setTA0001);

export const useTA0002 = () => useExtensionInfoStore((state) => state.TA0002);
export const useSetTA0002 = () => useExtensionInfoStore((state) => state.setTA0002);

export const useTA0003 = () => useExtensionInfoStore((state) => state.TA0003);
export const useSetTA0003 = () => useExtensionInfoStore((state) => state.setTA0003);

export const useTA0004 = () => useExtensionInfoStore((state) => state.TA0004);
export const useSetTA0004 = () => useExtensionInfoStore((state) => state.setTA0004);

export const useTA0005 = () => useExtensionInfoStore((state) => state.TA0005);
export const useSetTA0005 = () => useExtensionInfoStore((state) => state.setTA0005);

export const useTA0006 = () => useExtensionInfoStore((state) => state.TA0006);
export const useSetTA0006 = () => useExtensionInfoStore((state) => state.setTA0006);

export const useTA0007 = () => useExtensionInfoStore((state) => state.TA0007);
export const useSetTA0007 = () => useExtensionInfoStore((state) => state.setTA0007);

export const useTA0008 = () => useExtensionInfoStore((state) => state.TA0008);
export const useSetTA0008 = () => useExtensionInfoStore((state) => state.setTA0008);

export const useTA0009 = () => useExtensionInfoStore((state) => state.TA0009);
export const useSetTA0009 = () => useExtensionInfoStore((state) => state.setTA0009);

export const useTA0013 = () => useExtensionInfoStore((state) => state.TA0013);
export const useSetTA0013 = () => useExtensionInfoStore((state) => state.setTA0013);

export const useTA0014 = () => useExtensionInfoStore((state) => state.TA0014);
export const useSetTA0014 = () => useExtensionInfoStore((state) => state.setTA0014);

export const useTA0015 = () => useExtensionInfoStore((state) => state.TA0015);
export const useSetTA0015 = () => useExtensionInfoStore((state) => state.setTA0015);

export const useTA0016 = () => useExtensionInfoStore((state) => state.TA0016);
export const useSetTA0016 = () => useExtensionInfoStore((state) => state.setTA0016);

export const useTA0017 = () => useExtensionInfoStore((state) => state.TA0017);
export const useSetTA0017 = () => useExtensionInfoStore((state) => state.setTA0017);

export const useTA0018 = () => useExtensionInfoStore((state) => state.TA0018);
export const useSetTA0018 = () => useExtensionInfoStore((state) => state.setTA0018);

export const useTA0019 = () => useExtensionInfoStore((state) => state.TA0019);
export const useSetTA0019 = () => useExtensionInfoStore((state) => state.setTA0019);

export const useTA0020 = () => useExtensionInfoStore((state) => state.TA0020);
export const useSetTA0020 = () => useExtensionInfoStore((state) => state.setTA0020);

export const useTA0021 = () => useExtensionInfoStore((state) => state.TA0021);
export const useSetTA0021 = () => useExtensionInfoStore((state) => state.setTA0021);

export const useTA0022 = () => useExtensionInfoStore((state) => state.TA0022);
export const useSetTA0022 = () => useExtensionInfoStore((state) => state.setTA0022);

export const useTA0023 = () => useExtensionInfoStore((state) => state.TA0023);
export const useSetTA0023 = () => useExtensionInfoStore((state) => state.setTA0023);

export const useTA0024 = () => useExtensionInfoStore((state) => state.TA0024);
export const useSetTA0024 = () => useExtensionInfoStore((state) => state.setTA0024);

export const useTA0025 = () => useExtensionInfoStore((state) => state.TA0025);
export const useSetTA0025 = () => useExtensionInfoStore((state) => state.setTA0025);

export const useTA0026 = () => useExtensionInfoStore((state) => state.TA0026);
export const useSetTA0026 = () => useExtensionInfoStore((state) => state.setTA0026);

export const useTA0027 = () => useExtensionInfoStore((state) => state.TA0027);
export const useSetTA0027 = () => useExtensionInfoStore((state) => state.setTA0027);

export const useTA0090 = () => useExtensionInfoStore((state) => state.TA0090);
export const useSetTA0090 = () => useExtensionInfoStore((state) => state.setTA0090);

export const useTA0091 = () => useExtensionInfoStore((state) => state.TA0091);
export const useSetTA0091 = () => useExtensionInfoStore((state) => state.setTA0091);

export const useTA0092 = () => useExtensionInfoStore((state) => state.TA0092);
export const useSetTA0092 = () => useExtensionInfoStore((state) => state.setTA0092);

export const useTA0093 = () => useExtensionInfoStore((state) => state.TA0093);
export const useSetTA0093 = () => useExtensionInfoStore((state) => state.setTA0093);

export const useTA0094 = () => useExtensionInfoStore((state) => state.TA0094);
export const useSetTA0094 = () => useExtensionInfoStore((state) => state.setTA0094);

export const useTA0095 = () => useExtensionInfoStore((state) => state.TA0095);
export const useSetTA0095 = () => useExtensionInfoStore((state) => state.setTA0095);