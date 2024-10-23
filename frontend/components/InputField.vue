<template>
  <div>
    <label :for="name">{{ label }}</label>
    <input :id="name" v-bind="$attrs" v-model="internalValue" @blur="onBlur" />
    <span v-if="errors && errors.length">{{ errors }}</span>
  </div>
</template>

<script>
import { useField } from 'vee-validate';
import * as yup from 'yup';

export default {
  name: 'InputField',
  props: {
    name: String,
    label: String,
    rules: String,
    modelValue: String // Thêm modelValue để đồng bộ với v-model từ bên ngoài
  },
  setup(props, { emit }) {
    const {value: fieldValue, errorMessage: errors, handleBlur: onBlur} = useField(
        props.name,
        yup.string().required('Trường này không được để trống')
    );

    // Đồng bộ fieldValue với v-model
    const internalValue = computed({
      get() {
        return props.modelValue;
      },
      set(value) {
        emit('update:modelValue', value);
        fieldValue.value = value; // Cập nhật vào useField
      }
    });

    return {
      internalValue, // Trả về giá trị để đồng bộ với v-model
      errors,
      onBlur
    };
  }
};
</script>

<style scoped>
span {
  color: red;
}
</style>
