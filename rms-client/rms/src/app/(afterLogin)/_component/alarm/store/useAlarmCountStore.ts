import {create, SetState} from 'zustand';

interface AlarmCountState {
    alarmCount: number;
    setAlarmCount: (newAlarmCount: number) => void;
}

const useAlarmCountStore = create<AlarmCountState>((set: SetState<AlarmCountState>) => ({
    alarmCount: 0,
    setAlarmCount: (newAlarmCount: number) => set({ alarmCount: newAlarmCount })
}));

export const useAlarmCount = () => useAlarmCountStore((state) => state.alarmCount);
export const useSetAlarmCount = () => useAlarmCountStore((state) => state.setAlarmCount);
