import {ref} from 'vue';
import {useRouter} from 'vue-router';

export default function useUser() {
    const UserInfoForm = ref({
        id: '',
        name: '',
        avatar: '',
        nickname: '',
    });

    const router = useRouter();

    const initUserSession = () => {
        const userBaseInfoFromSession = JSON.parse(sessionStorage.getItem('userBaseInfo'));
        if (userBaseInfoFromSession) {
            UserInfoForm.value.id = userBaseInfoFromSession.id;
            UserInfoForm.value.name = userBaseInfoFromSession.username;
            UserInfoForm.value.avatar = userBaseInfoFromSession.avatar;
            UserInfoForm.value.nickname = userBaseInfoFromSession.nickname;
        } else {
            router.push('/');
        }
    }

    return {
        UserInfoForm,
        initUserSession,
    }
}