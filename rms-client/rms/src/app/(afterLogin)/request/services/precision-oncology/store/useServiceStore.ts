import {create, SetState} from 'zustand';

interface Item {
    id: string;
    name: string;
}

interface ServiceState {
    service: Item[] | null;
    selectService: Item | null;
    setServices: (newOrganization: Item[] | null) => void;
    setSelectService: (newSelectOrganization: Item | null) => void;
}

const useServiceStore = create<ServiceState>((set: SetState<ServiceState>) => ({
    service: null,
    selectService: null,
    setServices: (newOrganization) => set({ service: newOrganization }),
    setSelectService: (newSelectOrganization) => set({ selectService: newSelectOrganization }),
}));

export const useService = () => useServiceStore((state) => state.service);
export const useSelectService = () => useServiceStore((state) => state.selectService);
export const useServicesAction = () => useServiceStore((state) => state.setServices);
export const useSelectServiceAction = () => useServiceStore((state) => state.setSelectService);