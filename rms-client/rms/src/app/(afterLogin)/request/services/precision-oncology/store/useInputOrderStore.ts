import {create, SetState} from 'zustand';

interface PatientState {
    name: string;
    setName: (newSetText: string) => void;
    mrn: string;
    setMrn: (newSetText: string) => void;
    age: number;
    setAge: (newSetText: number) => void;
}

interface SpecimenState {
    type: string;
    setType: (newSetText: string) => void;
    quantity: number;
    setQuantity: (newSetText: number) => void;
    memo: string;
    setMemo: (newSetText: string) => void;
}

interface AdditionalInfoState {
    medicalDepartment: string;
    setMedicalDepartment: (newSetText: string) => void;
    ward: string;
    setWard: (newSetText: string) => void;
    physician: string;
    setPhysician: (newSetText: string) => void;
}

const usePatientStore = create<PatientState>((set: SetState<PatientState>) => ({
    name: "",
    setName: (newSetText) => set({ name: newSetText }),
    mrn: "",
    setMrn: (newSetText) => set({ mrn: newSetText }),
    age: 0,
    setAge: (newSetText) => set({ age: newSetText }),
}));

const useSpecimenStore = create<SpecimenState>((set: SetState<SpecimenState>) => ({
    type: "",
    setType: (newSetText) => set({ type: newSetText }),
    quantity: 0,
    setQuantity: (newSetText) => set({ quantity: newSetText }),
    memo: "",
    setMemo: (newSetText) => set({ memo: newSetText }),
}));

const useAdditionalInfoStore = create<AdditionalInfoState>((set: SetState<AdditionalInfoState>) => ({
    medicalDepartment: "",
    setMedicalDepartment: (newSetText) => set({ medicalDepartment: newSetText }),
    ward: "",
    setWard: (newSetText) => set({ ward: newSetText }),
    physician: "",
    setPhysician: (newSetText) => set({ physician: newSetText }),
}));

export const useName = () => usePatientStore((state) => state.name);
export const useSetName = () => usePatientStore((state) => state.setName);
export const useMrn = () => usePatientStore((state) => state.mrn);
export const useSetMrn = () => usePatientStore((state) => state.setMrn);
export const useAge = () => usePatientStore((state) => state.age);
export const useSetAge = () => usePatientStore((state) => state.setAge);

export const useType = () => useSpecimenStore((state) => state.type);
export const useSetType = () => useSpecimenStore((state) => state.setType);
export const useQuantity = () => useSpecimenStore((state) => state.quantity);
export const useSetQuantity = () => useSpecimenStore((state) => state.setQuantity);
export const useMemo = () => useSpecimenStore((state) => state.memo);
export const useSetMemo = () => useSpecimenStore((state) => state.setMemo);

export const useMedicalDepartment = () => useAdditionalInfoStore((state) => state.medicalDepartment);
export const useSetMedicalDepartment = () => useAdditionalInfoStore((state) => state.setMedicalDepartment);
export const useWard = () => useAdditionalInfoStore((state) => state.ward);
export const useSetWard = () => useAdditionalInfoStore((state) => state.setWard);
export const usePhysician = () => useAdditionalInfoStore((state) => state.physician);
export const useSetPhysician = () => useAdditionalInfoStore((state) => state.setPhysician);