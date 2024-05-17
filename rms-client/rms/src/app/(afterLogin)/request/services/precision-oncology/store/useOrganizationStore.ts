import {create, SetState} from 'zustand';

interface Organization {
    id: string;
    name?: string;
}

interface OrganizationState {
    organization: Organization[] | null;
    selectOrganization: Organization | null;
    setOrganizations: (newOrganization: Organization[] | null) => void;
    setSelectOrganization: (newSelectOrganization: Organization | null) => void;
}

const useOrganizationStore = create<OrganizationState>((set: SetState<OrganizationState>) => ({
    organization: null,
    selectOrganization: null,
    setOrganizations: (newOrganization) => set({ organization: newOrganization }),
    setSelectOrganization: (newSelectOrganization) => set({ selectOrganization: newSelectOrganization }),
}));

export const useOrganization = () => useOrganizationStore((state) => state.organization);
export const useSelectOrganization = () => useOrganizationStore((state) => state.selectOrganization);
export const useOrganizationsAction = () => useOrganizationStore((state) => state.setOrganizations);
export const useSelectOrganizationAction = () => useOrganizationStore((state) => state.setSelectOrganization);