import {create, SetState} from 'zustand';

interface Service {
    id: string;
    name: string;
}

interface ServiceState {
    service: Service[];
    selectService: Service | null;
    setServices: (newService: Service[]) => void;
    setSelectService: (newSelectService: Service | null) => void;
}

const useServiceStore = create<ServiceState>((set: SetState<ServiceState>) => ({
    service: [],
    selectService: null,
    setServices: (newService) => set({ service: newService }),
    setSelectService: (newSelectService) => set({ selectService: newSelectService }),
}));

export const useService = () => useServiceStore((state) => state.service);
export const useSelectService = () => useServiceStore((state) => state.selectService);
export const useServicesAction = () => useServiceStore((state) => state.setServices);
export const useSelectServiceAction = () => useServiceStore((state) => state.setSelectService);