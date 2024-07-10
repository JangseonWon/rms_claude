import {create, SetState} from 'zustand';

interface Calendar {
    year: number;
    month: number;
    day: number;
    fullDate: string;
}

interface CalendarState {
    collection: Calendar;
    setCollection: (newCalendar: Calendar) => void;
    birth: Calendar | null;
    setBirth: (newCalendar: Calendar | null) => void;
}

const getCurrentDate = (): Calendar => {
    const now = new Date();
    const year = now.getFullYear();
    const month = now.getMonth() + 1;
    const day = now.getDate();
    const fullDate = now.toISOString().split('T')[0];

    return { year, month, day, fullDate };
};

const useCalendarStore = create<CalendarState>((set: SetState<CalendarState>) => ({
    collection: getCurrentDate(),
    setCollection: (newCalendar) => set({ collection: newCalendar }),
    birth: null,
    setBirth: (newSelectCalendar) => set({ birth: newSelectCalendar }),
}));

export const useCollection = () => useCalendarStore((state) => state.collection);
export const useSelectCollection = () => useCalendarStore((state) => state.setCollection);
export const useBirth = () => useCalendarStore((state) => state.birth);
export const useSelectBirth = () => useCalendarStore((state) => state.setBirth);